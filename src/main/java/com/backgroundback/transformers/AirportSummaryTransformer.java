package com.backgroundback.transformers;

import com.backgroundback.model.Airport;
import com.backgroundback.model.AirportSummary;
import com.backgroundback.model.WeatherConditions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.IOException;

public class AirportSummaryTransformer {

   private ObjectMapper objectMapper;

   public AirportSummaryTransformer(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
   }

   public String createAirportSummaryJsonObject(Airport airport, WeatherConditions weatherConditions) {
      try {
         return this.objectMapper.writeValueAsString(createAirportSummary(airport, weatherConditions));
      } catch (IOException e) {
         return "Failed to create JSON object from airport summary: " + e.getMessage();
      }
   }

   private AirportSummary createAirportSummary(Airport airport, WeatherConditions weatherConditions) {
      AirportSummary.AirportSummaryBuilder airportSummary = AirportSummary.builder();

      airportSummary.setAirportIdentifier(airport.getIcao());
      airportSummary.setAirportName(airport.getName());


      return airportSummary.build();
   }
}
