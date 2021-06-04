package com.backgroundback.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "set")
public class AirportSummary {
   String airportIdentifier;
   String airportName;
   String[] runways;
   double latitude;
   double longitude;

   CurrentWeatherReport currentWeatherReport;

   ForecastReport[] forecastReport;

   @Data
   @Builder(setterPrefix = "set")
   public static class CurrentWeatherReport {
      int tempF;
      int relativeHumidityPercent;

      // Text string.
      String greatestCloudCoverageSummary;

      int visibilitySM;
      int windSpeedMPH;

      // Cardinal directions to secondary-intercardinal precision.
      String cardinalWindDirection;
   }

   @Data
   @Builder(setterPrefix = "set")
   public static class ForecastReport {
      // hrs:min
      String offsetFromDateIssuedToThisPeriodHrsMins;

      int tempF;
      int windSpeedMPH;
      int windDirectionDegreesTrue;
   }
}
