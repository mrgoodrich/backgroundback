package com.backgroundback.networking;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.CompletableFuture;

public class AirportLoader {

   private OkHttpClient client;

   public AirportLoader(OkHttpClient okHttpClient) {
      this.client = okHttpClient;
   }

   public CompletableFuture<Response> getAirportInformation(String airportIdentifier) {
      Request request = new Request.Builder()
            .url("https://qa.foreflight.com/airports/" + airportIdentifier)
            .addHeader("ff-coding-exercise", "1")
            .build();

      OkHttpResponseFuture result = new OkHttpResponseFuture();

      client.newCall(request).enqueue(result);

      return result.future;
   }
}
