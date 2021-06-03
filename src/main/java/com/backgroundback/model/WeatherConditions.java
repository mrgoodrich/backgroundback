package com.backgroundback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherConditions {
   Report report;

   @Data
   @JsonIgnoreProperties(ignoreUnknown = true)
   public static class Report {
      Conditions conditions;
      Forecast forecast;
      WindsAloft windsAloft;

      @Data
      @JsonIgnoreProperties(ignoreUnknown = true)
      public static class Conditions {
         public Conditions() {}

         String text;
         String ident;
         String dateIssued;
         double lat;
         double lon;
         int elevationFt;
         int tempC;
         int dewpointC;
         double pressureHg;
         int densityAltitudeFt;
         int relativeHumidity;
         boolean autonomous;
         String flightRules;
         CloudLayers[] cloudLayers;
         CloudLayers[] cloudLayersV2;
         String[] weather;
         ForecastPeriod period;

         @Data
         @JsonIgnoreProperties(ignoreUnknown = true)
         public static class CloudLayers {
            public CloudLayers() {}

            String coverage;
            int altitudeFt;
            boolean ceiling;
         }

         @Data
         @JsonIgnoreProperties(ignoreUnknown = true)
         public static class Visibility {
            int distanceSm;
            int prevailingVisSm;
         }

         @Data
         @JsonIgnoreProperties(ignoreUnknown = true)
         public static class Wind {
            int speedKts;
            int variableFrom;
            int variableTo;
            int from;
            int to;
            boolean variable;
         }
      }

      @Data
      @JsonIgnoreProperties(ignoreUnknown = true)
      public static class Forecast {
         String text;
         String ident;
         String dateIssued;
         double lat;
         double lon;
         int elevationFt;
         Conditions[] conditions;
      }

      @Data
      @JsonIgnoreProperties(ignoreUnknown = true)
      public static class WindsAloft {
         double lat;
         double lon;
         String dateIssued;
         WindsAloftForecast[] windsAloft;
         String source;

         @Data
         @JsonIgnoreProperties(ignoreUnknown = true)
         public static class WindsAloftForecast {
            String validTime;
            ForecastPeriod period;
            Map<Integer, WindsTemps> windsTemps;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class WindsTemps {
               int direcitonFromTrue;
               int knots;
               int celsius;
               int altitude;
               boolean isLightAndVariable;
               boolean isGreaterThan199Knots;
               boolean turbulence;
               boolean icing;
            }
         }
      }

      @Data
      @JsonIgnoreProperties(ignoreUnknown = true)
      public static class ForecastPeriod {
         String dateStart;
         String dateEnd;
      }
   }
}
