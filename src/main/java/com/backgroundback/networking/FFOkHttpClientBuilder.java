package com.backgroundback.networking;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FFOkHttpClientBuilder {

   public static OkHttpClient createFFOkHttpClient() {
      OkHttpClient.Builder client = new OkHttpClient.Builder();
      client.authenticator(new Authenticator() {
         @Override
         public Request authenticate(Route route, Response response) throws IOException {
            String credential = Credentials.basic("ff-interview", "@-*KzU.*dtP9dkoE7PryL2ojY!uDV.6JJGC9");
            return response.request().newBuilder().header("Authorization", credential).build();
         }
      });
      client.connectTimeout(10, TimeUnit.SECONDS);
      client.writeTimeout(10, TimeUnit.SECONDS);
      client.readTimeout(30, TimeUnit.SECONDS);

      return client.build();
   }

}
