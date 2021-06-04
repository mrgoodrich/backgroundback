package com.backgroundback.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/* Transforms CompletableFutures into the desired Java POJO. */
public class ResponseTransformer {

   ObjectMapper objectMapper;

   public ResponseTransformer(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
   }

   /**
    * Transform a future into a Java POJO and handle exceptions appropriately for the airport summary tool.
    *
    * @param future the compeleted future.
    * @param type the desired POJO for Jackson mapping.
    * @param <T> generic type for the POJO.
    * @return an optional POJO if the future succeeded and Jackson mapped successfully.
    */
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
