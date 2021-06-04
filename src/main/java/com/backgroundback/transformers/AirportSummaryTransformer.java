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

/** Transforms API data to create the desired airport summary. */
public class AirportSummaryTransformer {

   // The conversion factor for knots to miles per hour.
   public static final double KTS_TO_MPH = 1.150779448;

   private final ObjectMapper objectMapper;

   public AirportSummaryTransformer(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
   }

   /**
    * Creates a beautified JSON object to output given API data for the airport and weather conditions.
    *
    * @param airport information from the API on the selected airport.
    * @param weatherConditions weather conditions fromt he API for the selected airport.
    * @return
    */
   public String createAirportSummaryJsonObject(Airport airport, WeatherConditions weatherConditions) {
      try {
         return this.objectMapper
               .writerWithDefaultPrettyPrinter()
               .writeValueAsString(createAirportSummary(airport, weatherConditions));
      } catch (IOException e) {
         return "Failed to create JSON object from airport summary: " + e.getMessage();
      }
   }

   /**
    * Transforms the API data into the desired airport summary Java object.
    *
    * @param airport the airport data received from the Airport API.
    * @param weatherConditions the weather conditions data received from the Weather Conditions API.
    * @return an AirportSummary Java object, which is eventually transformed into JSON output for this tool.
    */
   private AirportSummary createAirportSummary(Airport airport, WeatherConditions weatherConditions) {
      AirportSummary.AirportSummaryBuilder airportSummary = AirportSummary.builder();

      WeatherConditions.Report report = weatherConditions.getReport();
      Conditions conditions = report.getConditions();

      // Add selective data from the Airport API to the AirportSummary.
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

      // Add selective data from the METAR to the AirportSummary.
      CurrentWeatherReport.CurrentWeatherReportBuilder currentWeatherReport = CurrentWeatherReport.builder();
      currentWeatherReport.setTempF(celsiusToFahrenheit(conditions.getTempC()));
      currentWeatherReport
            .setRelativeHumidityPercent(conditions.getRelativeHumidity());
      currentWeatherReport.setGreatestCloudCoverageSummary(getGreatestCloudCoverage(conditions.getCloudLayers()));
      currentWeatherReport.setWindSpeedMPH(knotsToMph(conditions.getWind().getSpeedKts()));
      currentWeatherReport.setCardinalWindDirection(
            getSecondaryIntercardinalWindDirection(conditions.getWind().getFrom()));

      airportSummary.setCurrentWeatherReport(currentWeatherReport.build());

      // Add selective data fromt he TAF to the AirportSummary.
      Forecast forecast = report.getForecast();
      String forecastDateIssued = forecast.getDateIssued();
      // Get variation to later convert from true to magnetic.
      int magneticVariationWest = airport.getMagneticVariationWestOrEstimate();
      // The exercise instructions sound like [1] and [2] should be used, but [0] and [1] seem more intuitive.
      airportSummary.setForecastReport(new ForecastReport[]{
            createForecastReport(forecast.getConditions()[0], forecastDateIssued, magneticVariationWest),
            createForecastReport(forecast.getConditions()[1], forecastDateIssued, magneticVariationWest)
      });

      return airportSummary.build();
   }

   /**
    * Converts temperature in celsius to fahrenheit.
    *
    * @param celsius the input temperature in degrees celsius.
    * @return the output temperature in degrees fahrenheit.
    */
   private int celsiusToFahrenheit(double celsius) {
      return (int) Math.round(celsius * 1.8 + 32);
   }

   // Obscurations in readable format for textual output purposes.
   private static final List<String> READABLE_OBSCURATIONS =
         ImmutableList.of("Clear", "Few", "Scattered", "Broken", "Overcast");

   /**
    * Returns the greatest obscuration, prioritizing lower ceilings if there are multiple layers with the same amount.
    *
    * Includes altitude in a readable string if skies aren't clear.
    *
    * @param cloudLayers the cloud layers existing for the given condition.
    * @return a textual string describing the greatest cloud coverage.
    */
   private String getGreatestCloudCoverage(CloudLayers[] cloudLayers) {
      return Arrays.stream(cloudLayers).sorted()
            .map(layer -> READABLE_OBSCURATIONS.get(CLOUD_LAYER_PRIORITY_ASCENDING.indexOf(layer.getCoverage())) +
                  (layer.getCoverage().equals("clr") ? "" : " at " + layer.getAltitudeFt() + "ft"))
            .findFirst()
            .orElse("Unknown");
   }

   /**
    * Converts from knots to miles per hour. Used for wind velocity calculations.
    *
    * @param kts the input knots to be converted.
    * @return the converted miles per hour.
    */
   private int knotsToMph(int kts) {
      return (int) Math.round(kts * KTS_TO_MPH);
   }

   // All secondary intercardinal directions clockwise starting from North.
   private static final List<String> SECONDARY_INTERCARDINAL_WIND_DIRECTIONS =
         ImmutableList
               .of("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW","NNW");
   // The number of degrees for each secondary intercardinal direction.
   private static final double NUM_DEGREES_PER_SECONDARY_INTERCARDINAL_DIRECTION = 22.5;

   /**
    * Converts the given degrees into the associated secondary intercardinal wind direction.
    *
    * @param degrees the selected degrees, 001 to 360.
    * @return a textual secondary intercardinal wind direction.
    */
   private String getSecondaryIntercardinalWindDirection(int degrees) {
      // Edge case - north counts for both lowest and highest number of degrees.
      if (degrees > (360 - NUM_DEGREES_PER_SECONDARY_INTERCARDINAL_DIRECTION / 2)) {
         return "N";
      }

      // Start negative to simplify adding algorithm.
      double currentDegree = 0 - NUM_DEGREES_PER_SECONDARY_INTERCARDINAL_DIRECTION / 2;

      // Loop through each direction and check if the current degrees fall in that segment.
      for (String direction : SECONDARY_INTERCARDINAL_WIND_DIRECTIONS) {
         if (currentDegree < degrees && degrees < currentDegree + NUM_DEGREES_PER_SECONDARY_INTERCARDINAL_DIRECTION) {
            return direction;
         }
         currentDegree += NUM_DEGREES_PER_SECONDARY_INTERCARDINAL_DIRECTION;
      }
      return "Unknown";
   }

   // Matches the date time format received from the Weather Conditions API.
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

   /**
    * Creates a ForecastReport to insert in the output AirportSummary.
    *
    * @param condition one of the conditions taken from the TAF.
    * @param forecastDateIssued the instant the TAF was issued for comparison purposes.
    * @param magneticVariationWest the variation of the airport associated with the ForecastReport.
    * @return a ForecastReport for eventual output.
    */
   private ForecastReport createForecastReport(Conditions condition,
                                               String forecastDateIssued,
                                               int magneticVariationWest) {
      ForecastReport.ForecastReportBuilder forecastReportBuilder = ForecastReport.builder();

      forecastReportBuilder.setOffsetFromDateIssuedToThisPeriodHrsMins(getTimeOffset(condition, forecastDateIssued));
      forecastReportBuilder.setTempF(celsiusToFahrenheit(condition.getTempC()));
      forecastReportBuilder.setWindSpeedMPH(knotsToMph(condition.getWind().getSpeedKts()));
      // East is least, west is best. (pneumonic from instructing)
      forecastReportBuilder.setWindDirectionDegreesTrue(condition.getWind().getFrom() + magneticVariationWest);

      return forecastReportBuilder.build();
   }

   /**
    * Calculates how long the selected TAF period starts after the time the TAF was issued.
    *
    * For example, if the TAF was issued three hours and fifteen minutes before the given condition is forecast to
    * appear, then the method will return "3:15".
    *
    * @param condition one of the conditions of the TAF.
    * @param forecastDateIssued the date time when the TAF was issued.
    * @return a string containing the hours and minutes.
    */
   private String getTimeOffset(Conditions condition, String forecastDateIssued) {
      LocalDateTime dateTimeForecastIssued = LocalDateTime.parse(forecastDateIssued, DATE_TIME_FORMATTER);
      LocalDateTime startOfThisPeriod = LocalDateTime.parse(condition.getPeriod().getDateStart(), DATE_TIME_FORMATTER);

      long hours = dateTimeForecastIssued.until(startOfThisPeriod, ChronoUnit.HOURS);
      long minutes = dateTimeForecastIssued.until(startOfThisPeriod, ChronoUnit.MINUTES);
      return hours + ":" + (minutes % 60);
   }
}
