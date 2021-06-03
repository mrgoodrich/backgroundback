package com.backgroundback;

import com.backgroundback.model.WeatherConditions;
import com.backgroundback.networking.WeatherConditionsLoader;
import com.backgroundback.userinput.InputParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {

   public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);
      InputParser parser = new InputParser();
      OkHttpClient client = new OkHttpClient();
      WeatherConditionsLoader weatherConditionsLoader = new WeatherConditionsLoader(client);

      System.out.println("Please enter one or more airport identifiers:");
      while (scanner.hasNextLine()) {
         String airportIdentifiersRaw = scanner.nextLine();

         System.out.println("Entered " + airportIdentifiersRaw);
         parser.parseAirportIdentifiers(airportIdentifiersRaw).stream().forEach(id -> {
            CompletableFuture weatherFuture = weatherConditionsLoader.getWeatherConditions(id);

            try {

               Response weatherResponse = (Response) weatherFuture.get();
               System.out.println(weatherResponse.body().string());

               ObjectMapper objectMapper = new ObjectMapper();
               WeatherConditions weatherConditions = objectMapper.readValue(weatherResponse.body().string(), WeatherConditions.class);

               System.out.println(weatherConditions.getReport().getConditions().getText());


            } catch (Exception exception) {
               System.out.println(exception);
            }
         });
      }
   }
}
