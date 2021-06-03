package com.backgroundback.runner;

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
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class AirportSummaryRunner {

   private final Scanner scanner;
   private final InputParser parser;

   private final WeatherConditionsLoader weatherConditionsLoader;
   private final AirportLoader airportLoader;

   private final ResponseTransformer responseTransformer;
   private final AirportSummaryTransformer airportSummaryTransformer;

   public AirportSummaryRunner() {
      this.scanner = new Scanner(System.in);
      this.parser = new InputParser();

      OkHttpClient client = FFOkHttpClientBuilder.createFFOkHttpClient();
      ObjectMapper objectMapper = new ObjectMapper();

      this.weatherConditionsLoader = new WeatherConditionsLoader(client);
      this.airportLoader = new AirportLoader(client);

      this.responseTransformer = new ResponseTransformer(objectMapper);
      this.airportSummaryTransformer = new AirportSummaryTransformer(objectMapper);

      runAirportSummaryTool();
   }

   private void runAirportSummaryTool() {
      printIntroAndInstructions();

      while (scanner.hasNextLine()) {
         String airportIdentifiersRaw = scanner.nextLine();
         List<String> airportIds = parser.parseAirportIdentifiers(airportIdentifiersRaw);

         System.out.println("Loading airport summaries: " + String.join(", ", airportIds));

         for (String id : airportIds) {
            loadAirportSummary(id);
         }
      }
   }

   private void loadAirportSummary(String id) {
      CompletableFuture<Response> weatherFuture = weatherConditionsLoader.getWeatherConditions(id);
      CompletableFuture<Response> airportFuture = airportLoader.getAirportInformation(id);

      CompletableFuture.allOf(weatherFuture, airportFuture)
            .whenComplete(processAirportSummary(weatherFuture, airportFuture));
   }

   @NotNull
   private BiConsumer<Void, Throwable> processAirportSummary(CompletableFuture<Response> weatherFuture,
                                                             CompletableFuture<Response> airportFuture) {
      return (unused, throwable) -> {
         Optional<Airport> airport = responseTransformer.transformResponse(airportFuture, Airport.class);
         Optional<WeatherConditions> weatherConditions =
               responseTransformer.transformResponse(weatherFuture, WeatherConditions.class);

         if (weatherConditions.isPresent() && airport.isPresent()) {
            String airportSummary =
                  airportSummaryTransformer
                        .createAirportSummaryJsonObject(airport.get(), weatherConditions.get());
            System.out.println(airportSummary);
         }
      };
   }

   private void printIntroAndInstructions() {
      System.out.println("********************************************************");
      System.out.println("*  Airport Summary Tool     by Matt Goodrich           *");
      System.out.println("*                                                      *");
      System.out.println("*  Usage: Enter one or more ICAO airport identifiers.  *");
      System.out.println("*                                                      *");
      System.out.println("*  For example: kpwm, kaus                             *");
      System.out.println("*  You can enter more identifiers at any time.         *");
      System.out.println("********************************************************\n");
   }
}
