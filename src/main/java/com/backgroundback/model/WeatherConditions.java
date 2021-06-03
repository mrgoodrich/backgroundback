package com.backgroundback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherConditions {
   public Report report;

   @Data
   @JsonIgnoreProperties(ignoreUnknown = true)
   public class Report {
      public Conditions conditions;

      @Data
      @JsonIgnoreProperties(ignoreUnknown = true)
      public class Conditions {
         public String text;
      }
   }
}
