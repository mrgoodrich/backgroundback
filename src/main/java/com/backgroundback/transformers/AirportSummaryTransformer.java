package com.backgroundback.transformers;

import com.backgroundback.model.Airport;
import com.backgroundback.model.AirportSummary;
import com.backgroundback.model.AirportSummary.CurrentWeatherReport;
import com.backgroundback.model.WeatherConditions;
import com.backgroundback.model.WeatherConditions.Report.Conditions.CloudLayers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
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
      WeatherConditions.Report.Conditions conditions = report.getConditions();

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
      airportSummary.setCurrentWeatherReport(currentWeatherReport.build());

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
}
