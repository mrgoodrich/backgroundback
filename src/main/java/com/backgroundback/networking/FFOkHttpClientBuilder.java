package com.backgroundback.networking;

import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class FFOkHttpClientBuilder {

   public static OkHttpClient createFFOkHttpClient() {
      OkHttpClient.Builder client = new OkHttpClient.Builder();
      client.authenticator((route, response) -> {
         String credential = Credentials.basic("ff-interview", "@-*KzU.*dtP9dkoE7PryL2ojY!uDV.6JJGC9");
         return response.request().newBuilder().header("Authorization", credential).build();
      });
      client.connectionPool(new ConnectionPool(30,30, TimeUnit.SECONDS));
      client.connectTimeout(30, TimeUnit.SECONDS);
      client.writeTimeout(30, TimeUnit.SECONDS);
      client.readTimeout(30, TimeUnit.SECONDS);

      return client.build();
   }
}
