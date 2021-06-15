package com.backgroundback.networking;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/** Manages requests to the airport API. */
public class AirportLoader {

   private OkHttpClient client;

   public AirportLoader(OkHttpClient okHttpClient) {
      this.client = okHttpClient;
   }

   /**
    * Submits a request to the airport API for the given identifier.
    *
    * @param airportIdentifier the airport ICAO identifier.
    * @return an asynchronous future containing the airport data response.
    */
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
