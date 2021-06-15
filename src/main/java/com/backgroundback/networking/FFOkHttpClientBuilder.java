package com.backgroundback.networking;

import okhttp3.*;

import java.io.File;
import java.io.IOException;
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
      File cacheDir = new File("backgroundback-cache");
      try {
         cacheDir.mkdirs();
      } catch (Exception e) {
         System.out.println("Failed to create cache");
      }
      Cache cache = new Cache(cacheDir, 50L * 1024L * 1024 * 100);

      // Clear cache on each program run.
      try {
         cache.evictAll();
      } catch (IOException e) {
         System.out.println("Failed to clear cache on startup");
      }

      client
              .addNetworkInterceptor(new CacheInterceptor())
              .cache(cache);

      return client.build();
   }

   // https://stackoverflow.com/questions/49453564/how-to-cache-okhttp-response-from-web-server
   public static class CacheInterceptor implements Interceptor {
      @Override
      public Response intercept(Chain chain) throws IOException {
         Response response = chain.proceed(chain.request());

         CacheControl cacheControl = new CacheControl.Builder()
                 .maxAge(15, TimeUnit.MINUTES)
                 .build();

         return response.newBuilder()
                 .removeHeader("Pragma")
                 .removeHeader("Cache-Control")
                 .header("Cache-Control", cacheControl.toString())
                 .build();
      }
   }
}


