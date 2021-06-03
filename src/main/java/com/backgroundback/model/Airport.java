package com.backgroundback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Airport {
   String icao;
   String name;

   @Data
   @JsonIgnoreProperties(ignoreUnknown = true)
   public static class Runway {
      String ident;
   }
}
