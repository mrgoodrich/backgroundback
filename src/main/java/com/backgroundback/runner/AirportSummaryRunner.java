package com.backgroundback.runner;

import com.backgroundback.model.Airport;
import com.backgroundback.model.WeatherConditions;
import com.backgroundback.networking.AirportLoader;
import com.backgroundback.networking.FFOkHttpClientBuilder;
import com.backgroundback.networking.WeatherConditionsLoader;
import com.backgroundback.transformers.AirportSummaryTransformer;
import com.backgroundback.transformers.ResponseTransformer;
import com.backgroundback.userinput.InputParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class AirportSummaryRunner {

   public static void runAirportSummaryTool() {
      Scanner scanner = new Scanner(System.in);
      InputParser parser = new InputParser();

      OkHttpClient client = FFOkHttpClientBuilder.createFFOkHttpClient();
      ObjectMapper objectMapper = new ObjectMapper();

      WeatherConditionsLoader weatherConditionsLoader = new WeatherConditionsLoader(client);
      AirportLoader airportLoader = new AirportLoader(client);

      ResponseTransformer responseTransformer = new ResponseTransformer(objectMapper);
      AirportSummaryTransformer airportSummaryTransformer = new AirportSummaryTransformer(objectMapper);


      while (scanner.hasNextLine()) {
         String airportIdentifiersRaw = scanner.nextLine();

         List<String> airportIds = parser.parseAirportIdentifiers(airportIdentifiersRaw);

         System.out.println("Loading airport summaries: " + String.join(", ", airportIds));

         for (String id : airportIds) {
            CompletableFuture<Response> weatherFuture = weatherConditionsLoader.getWeatherConditions(id);
            CompletableFuture<Response> airportFuture = airportLoader.getAirportInformation(id);

            CompletableFuture.allOf(weatherFuture, airportFuture).whenComplete((unused, throwable) -> {
               Optional<Airport> airport = responseTransformer.transformResponse(airportFuture, Airport.class);
               Optional<WeatherConditions> weatherConditions =
                     responseTransformer.transformResponse(weatherFuture, WeatherConditions.class);

               if (weatherConditions.isPresent() && airport.isPresent()) {
                  String airportSummary =
                        airportSummaryTransformer
                              .createAirportSummaryJsonObject(airport.get(), weatherConditions.get());
                  System.out.println(airportSummary);
               }
            });
         }
      }
   }
}
