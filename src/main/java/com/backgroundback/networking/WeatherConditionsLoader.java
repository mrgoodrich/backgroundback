package com.backgroundback.networking;

import okhttp3.*;

import java.util.concurrent.CompletableFuture;

public class WeatherConditionsLoader {

   private OkHttpClient client;

   public WeatherConditionsLoader(OkHttpClient okHttpClient) {
      this.client = okHttpClient;
   }

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
