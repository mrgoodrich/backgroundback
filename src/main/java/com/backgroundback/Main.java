package com.backgroundback;

import com.backgroundback.model.Airport;
import com.backgroundback.model.AirportSummary;
import com.backgroundback.model.WeatherConditions;
import com.backgroundback.networking.AirportLoader;
import com.backgroundback.networking.FFOkHttpClientBuilder;
import com.backgroundback.networking.WeatherConditionsLoader;
import com.backgroundback.transformers.AirportSummaryTransformer;
import com.backgroundback.transformers.ResponseTransformer;
import com.backgroundback.userinput.InputParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import okhttp3.*;

import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {

   public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);
      InputParser parser = new InputParser();

      OkHttpClient client = FFOkHttpClientBuilder.createFFOkHttpClient();
      ObjectMapper objectMapper = new ObjectMapper();

      WeatherConditionsLoader weatherConditionsLoader = new WeatherConditionsLoader(client);
      AirportLoader airportLoader = new AirportLoader(client);

      ResponseTransformer responseTransformer = new ResponseTransformer(objectMapper);
      AirportSummaryTransformer airportSummaryTransformer = new AirportSummaryTransformer(objectMapper);

      System.out.println("Please enter one or more airport identifiers:");
      while (scanner.hasNextLine()) {
         String airportIdentifiersRaw = scanner.nextLine();

         System.out.println("Entered " + airportIdentifiersRaw);
         parser.parseAirportIdentifiers(airportIdentifiersRaw).stream().forEach(id -> {
            CompletableFuture weatherFuture = weatherConditionsLoader.getWeatherConditions(id);
            CompletableFuture airportFuture = airportLoader.getAirportInformation(id);

            CompletableFuture.allOf(weatherFuture, airportFuture).join();

            Optional<Airport> airport = responseTransformer.transformResponse(airportFuture, Airport.class);
            Optional<WeatherConditions> weatherConditions =
                  responseTransformer.transformResponse(weatherFuture, WeatherConditions.class);

            if (weatherConditions.isPresent() && airport.isPresent()) {
               String airportSummary =
                     airportSummaryTransformer.createAirportSummaryJsonObject(airport.get(), weatherConditions.get());
               System.out.println(airportSummary);
            }
         });
      }
   }
}
