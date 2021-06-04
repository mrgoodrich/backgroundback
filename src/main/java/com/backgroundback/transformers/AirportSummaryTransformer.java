package com.backgroundback.transformers;

import com.backgroundback.model.Airport;
import com.backgroundback.model.AirportSummary;
import com.backgroundback.model.AirportSummary.CurrentWeatherReport;
import com.backgroundback.model.AirportSummary.ForecastReport;
import com.backgroundback.model.WeatherConditions;
import com.backgroundback.model.WeatherConditions.Report.Conditions;
import com.backgroundback.model.WeatherConditions.Report.Conditions.CloudLayers;
import com.backgroundback.model.WeatherConditions.Report.Forecast;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static com.backgroundback.model.WeatherConditions.Report.Conditions.CLOUD_LAYER_PRIORITY_ASCENDING;

public class AirportSummaryTransformer {

   public static final double KTS_TO_MPH = 1.150779448;
   private ObjectMapper objectMapper;

   public AirportSummaryTransformer(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
   }

   public String createAirportSummaryJsonObject(Airport airport, WeatherConditions weatherConditions) {
      try {
         return this.objectMapper
               .writerWithDefaultPrettyPrinter()
               .writeValueAsString(createAirportSummary(airport, weatherConditions));
      } catch (IOException e) {
         return "Failed to create JSON object from airport summary: " + e.getMessage();
      }
   }

   private AirportSummary createAirportSummary(Airport airport, WeatherConditions weatherConditions) {
      AirportSummary.AirportSummaryBuilder airportSummary = AirportSummary.builder();

      WeatherConditions.Report report = weatherConditions.getReport();
      Conditions conditions = report.getConditions();

      // Some airports don't specify ident in airport info, so default to identifier in weather report.
      airportSummary.setAirportIdentifier(
            airport.getIcao() != null ? airport.getIcao()
                  : conditions.getIdent());
      airportSummary.setAirportName(airport.getName());
      airportSummary.setRunways(
            Arrays.stream(airport.getRunways())
                  .map(Airport.Runway::getIdent)
                  .toArray(String[]::new));
      airportSummary.setLatitude(airport.getLatitude());
      airportSummary.setLongitude(airport.getLongitude());

      CurrentWeatherReport.CurrentWeatherReportBuilder currentWeatherReport = CurrentWeatherReport.builder();
      currentWeatherReport.setTempF(celsiusToFahrenheit(conditions.getTempC()));
      currentWeatherReport
            .setRelativeHumidityPercent(conditions.getRelativeHumidity());
      currentWeatherReport.setGreatestCloudCoverageSummary(getGreatestCloudCoverage(conditions.getCloudLayers()));
      currentWeatherReport.setWindSpeedMPH(knotsToMph(conditions.getWind().getSpeedKts()));
      currentWeatherReport.setCardinalWindDirection(
            getSecondaryIntercardinalWindDirection(conditions.getWind().getFrom()));

      airportSummary.setCurrentWeatherReport(currentWeatherReport.build());

      Forecast forecast = report.getForecast();
      String forecastDateIssued = forecast.getDateIssued();
      int magneticVariationWest = airport.getMagneticVariationWestOrEstimate();
      airportSummary.setForecastReport(new ForecastReport[]{
            createForecastReport(forecast.getConditions()[0], forecastDateIssued, magneticVariationWest),
            createForecastReport(forecast.getConditions()[1], forecastDateIssued, magneticVariationWest)
      });

      return airportSummary.build();
   }

   private int celsiusToFahrenheit(double celsius) {
      return (int) Math.round(celsius * 1.8 + 32);
   }

   private static final List<String> READABLE_OBSCURATIONS =
         ImmutableList.of("Clear", "Few", "Scattered", "Broken", "Overcast");

   /**
    * Returns the greatest obscuration, prioritizing lower ceilings if there are multiple layers with the same amount.
    *
    * Includes altitude in a readable string if skies aren't clear.
    *
    * @param cloudLayers
    * @return
    */
   private String getGreatestCloudCoverage(CloudLayers[] cloudLayers) {
      return Arrays.stream(cloudLayers).sorted()
            .map(layer -> READABLE_OBSCURATIONS.get(CLOUD_LAYER_PRIORITY_ASCENDING.indexOf(layer.getCoverage())) +
                  (layer.getCoverage().equals("clr") ? "" : " at " + layer.getAltitudeFt() + "ft"))
            .findFirst()
            .orElse("Unknown");
   }

   private int knotsToMph(int kts) {
      return (int) Math.round(kts * KTS_TO_MPH);
   }

   private static final List<String> SECONDARY_INTERCARDINAL_WIND_DIRECTIONS =
         ImmutableList
               .of("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW","NNW");
   private static final double NUM_DEGREES_PER_SECONDARY_INTERCARDINAL_DIRECTION = 22.5;

   private String getSecondaryIntercardinalWindDirection(int from) {
      // Edge case - north counts for both lowest and highest number of degrees.
      if (from > (360 - NUM_DEGREES_PER_SECONDARY_INTERCARDINAL_DIRECTION / 2)) {
         return "N";
      }

      // Start negative to simplify adding algorithm.
      double currentDegree = 0 - NUM_DEGREES_PER_SECONDARY_INTERCARDINAL_DIRECTION / 2;

      for (String direction : SECONDARY_INTERCARDINAL_WIND_DIRECTIONS) {
         if (currentDegree < from && from < currentDegree + NUM_DEGREES_PER_SECONDARY_INTERCARDINAL_DIRECTION) {
            return direction;
         }
         currentDegree += NUM_DEGREES_PER_SECONDARY_INTERCARDINAL_DIRECTION;
      }
      return "Unknown";
   }

   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

   private ForecastReport createForecastReport(Conditions condition,
                                               String forecastDateIssued,
                                               int magneticVariationWest) {
      ForecastReport.ForecastReportBuilder forecastReportBuilder = ForecastReport.builder();

      forecastReportBuilder.setOffsetFromDateIssuedToThisPeriodHrsMins(getTimeOffset(condition, forecastDateIssued));
      forecastReportBuilder.setTempF(celsiusToFahrenheit(condition.getTempC()));
      forecastReportBuilder.setWindSpeedMPH(knotsToMph(condition.getWind().getSpeedKts()));
      forecastReportBuilder.setWindDirectionDegreesTrue(condition.getWind().getFrom() + magneticVariationWest);

      return forecastReportBuilder.build();
   }

   private String getTimeOffset(Conditions condition, String forecastDateIssued) {
      LocalDateTime dateTimeForecastIssued = LocalDateTime.parse(forecastDateIssued, DATE_TIME_FORMATTER);
      LocalDateTime startOfThisPeriod = LocalDateTime.parse(condition.getPeriod().getDateStart(), DATE_TIME_FORMATTER);

      long hours = dateTimeForecastIssued.until(startOfThisPeriod, ChronoUnit.HOURS);
      long minutes = dateTimeForecastIssued.until(startOfThisPeriod, ChronoUnit.MINUTES);
      return hours + ":" + (minutes % 60);
   }
}
