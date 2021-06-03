package com.backgroundback.transformers;

import com.backgroundback.model.Airport;
import com.backgroundback.model.WeatherConditions;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ResponseTransformer {

   ObjectMapper objectMapper;

   public ResponseTransformer(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
   }

   public <T> Optional<T> transformResponse(CompletableFuture future, Class<T> type) {
      try {
         Response response = (Response) future.get();
         String responseBody = response.body().string();
//         System.out.println(responseBody);
         return Optional.of(objectMapper.readValue(responseBody, type));
      } catch (InterruptedException | ExecutionException e) {
         System.out.println("Failed to get " + type.getName());
      } catch (IOException e) {
         System.out.println("Failed to parse " + type.getName());
      }
      return Optional.empty();
   }
}
