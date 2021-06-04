package com.backgroundback.networking;

import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/** Builds an OkHttpClient for interaction with the target server. */
public class FFOkHttpClientBuilder {

   /**
    * Configures an OkHttpClient for the target server, including setting BasicAuth username and password.
    *
    * @return an OkHttpClient ready to use with the weather and airport API's.
    */
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
