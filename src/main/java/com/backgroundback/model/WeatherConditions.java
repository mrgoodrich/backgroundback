package com.backgroundback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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

         // For testing.
         public Conditions(String periodDateStart) {
            this.period = new ForecastPeriod(periodDateStart);
         }

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
         Visibility visibility;
         Wind wind;
         ForecastPeriod period;

         public static final List<String> CLOUD_LAYER_PRIORITY_ASCENDING =
               ImmutableList.of("clr", "few", "sct", "bkn", "ovc");

         @Data
         @JsonIgnoreProperties(ignoreUnknown = true)
         @AllArgsConstructor
         public static class CloudLayers implements Comparable<CloudLayers> {
            public CloudLayers() {}

            String coverage;
            int altitudeFt;
            boolean ceiling;

            @Override
            public int compareTo(@NotNull CloudLayers other) {
               int thisCoverage = getCoverageValue(this.coverage);
               int otherCoverage = getCoverageValue(other.coverage);

               // More obscuration means greater coverage.
               if (thisCoverage > otherCoverage) {
                  return -1;
               } else if (thisCoverage < otherCoverage) {
                  return 1;
               }

               // If the same obscuration, then use lower ceiling.
               if (this.altitudeFt < other.altitudeFt) {
                  return -1;
               } else if (this.altitudeFt > other.altitudeFt) {
                  return 1;
               }
               return 0;
            }

            private int getCoverageValue(String coverage) {
               return CLOUD_LAYER_PRIORITY_ASCENDING.indexOf(coverage.toLowerCase());
            }
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

         // For testing.
         public ForecastPeriod(String dateStart) {
            this.dateStart = dateStart;
         }
      }
   }
}
