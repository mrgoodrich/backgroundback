package com.backgroundback.networking;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.CompletableFuture;

/** Manages requests to the weather conditions API. */
public class WeatherConditionsLoader {

   private OkHttpClient client;

   public WeatherConditionsLoader(OkHttpClient okHttpClient) {
      this.client = okHttpClient;
   }

   /**
    * Submits a request to the weather conditions API for the given identifier.
    *
    * @param airportIdentifier the airport ICAO identifier.
    * @return an asynchronous future containing the weather conditions data response.
    */
   public CompletableFuture<Response> getWeatherConditions(String airportIdentifier) {
      Request request = new Request.Builder()
            .url("https://qa.foreflight.com/weather/report/" + airportIdentifier)
            .addHeader("ff-coding-exercise", "1")
            .build();

      OkHttpResponseFuture result = new OkHttpResponseFuture();

      client.newCall(request).enqueue(result);

      return result.future;
   }
}
