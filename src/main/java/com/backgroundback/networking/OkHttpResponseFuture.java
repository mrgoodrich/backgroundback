package com.backgroundback.networking;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Bridges OkHttp Callback to to Java futures.
 * https://stackoverflow.com/questions/41833314/how-to-make-concurrent-network-requests-using-okhttp/41840226
 */
public class OkHttpResponseFuture implements Callback {
   public final CompletableFuture<Response> future = new CompletableFuture<>();

   public OkHttpResponseFuture() {
   }

   @Override public void onFailure(Call call, IOException e) {
      future.completeExceptionally(e);
   }

   @Override public void onResponse(Call call, Response response) throws IOException {
      future.complete(response);
   }
}
