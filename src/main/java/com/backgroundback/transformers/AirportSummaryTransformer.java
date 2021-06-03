package com.backgroundback.transformers;

import com.backgroundback.model.Airport;
import com.backgroundback.model.AirportSummary;
import com.backgroundback.model.WeatherConditions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.IOException;
import java.util.Arrays;

public class AirportSummaryTransformer {

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

      // Some airports don't specify ident in airport info, so default to identifier in weather report.
      airportSummary.setAirportIdentifier(
            airport.getIcao() != null ? airport.getIcao()
                  : weatherConditions.getReport().getConditions().getIdent());
      airportSummary.setAirportName(airport.getName());
      airportSummary.setRunways(
            Arrays.stream(airport.getRunways())
                  .map(Airport.Runway::getIdent)
                  .toArray(String[]::new));
      airportSummary.setLatitude(airport.getLatitude());
      airportSummary.setLongitude(airport.getLongitude());

      return airportSummary.build();
   }
}
