package io.github.flamehub.tiktok;

import io.github.flamehub.commons.json.JsonUtil;
import io.github.flamehub.tiktok.account.TikTokAccount;
import io.github.flamehub.tiktok.account.TikTokAccountWrapper;
import io.github.flamehub.tiktok.video.TikTokVideoData;
import io.github.flamehub.tiktok.video.TikTokVideoFetchException;
import io.github.flamehub.tiktok.video.TikTokVideoWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class TikTokService {

  private final static String API_KEY = "2e0613d978msh7a65897d638e4fdp15a618jsnbee06d86de01";
  private static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
      .connectTimeout(10, TimeUnit.SECONDS)
      .readTimeout(10, TimeUnit.SECONDS)
      .writeTimeout(10, TimeUnit.SECONDS)
      .build();

  public static void main(final String[] args) {
    final TikTokService tikTokService = new TikTokService();
    final CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
      try {
        final TikTokAccount tikTokAccount = tikTokService.fetchTikTokAccount("flamehub.pl");
        System.out.println(tikTokAccount);
        return tikTokAccount;
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }).thenAcceptAsync(tikTokAccount -> {
      final List<TikTokVideoWrapper> tikTokVideoWrappers;
      try {
        tikTokVideoWrappers = tikTokService.fetchVideos(
            "MS4wLjABAAAAezJaYWl6LhkCMHNZnkYCIRvR3m8gpzvYgVgLPxqpK136mgMwEC9-ci55DsYB3sfg");
      } catch (final IOException | TikTokVideoFetchException e) {
        throw new RuntimeException(e);
      }
      System.out.println(tikTokVideoWrappers);
    });

    future.join();
  }


  public List<TikTokVideoWrapper> fetchVideos(final String secUid)
      throws TikTokVideoFetchException, IOException {

    final Request request = new Request.Builder()
        .url("https://tiktok-api23.p.rapidapi.com/api/user/posts?secUid=" + secUid
            + "&count=100&cursor=0")
        .get()
        .addHeader("x-rapidapi-key", "2e0613d978msh7a65897d638e4fdp15a618jsnbee06d86de01")
        .addHeader("x-rapidapi-host", "tiktok-api23.p.rapidapi.com")
        .build();

    final Response response = CLIENT.newCall(request).execute();
    if (!response.isSuccessful()) {
      // Lepsze logowanie błędu z kodem statusu i odpowiedzią
      final String errorBody = response.body() != null ? response.body().string() : "no body";
      throw new TikTokVideoFetchException("HTTP " + response.code() + ": " + response.message() + " - " + errorBody);
    }

    try (final ResponseBody body = response.body()) {
      final String string = body.string();
      if (string.isEmpty()) {
        throw new TikTokVideoFetchException("string.isEmpty()");
      }

      final TikTokVideoData tikTokVideoData = JsonUtil.GSON.fromJson(string,
          TikTokVideoData.class);
      
      // Sprawdzamy czy dane nie są null
      if (tikTokVideoData == null || tikTokVideoData.getTikTokVideosWrapper() == null) {
        throw new TikTokVideoFetchException("tikTokVideoData or wrapper is null");
      }
      
      final List<TikTokVideoWrapper> allVideos = tikTokVideoData.getTikTokVideosWrapper().getPosts();
      
      // Sprawdzamy czy lista filmów nie jest null
      if (allVideos == null) {
        return new ArrayList<>(); // Zwracamy pustą listę zamiast null
      }
      
      // Filtrowanie filmów z ostatnich 30 dni i z odpowiednimi hashtagami
      final long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
      final List<TikTokVideoWrapper> filteredVideos = new ArrayList<>();
      
      for (final TikTokVideoWrapper video : allVideos) {
        // Sprawdzamy czy video nie jest null
        if (video == null) {
          continue;
        }
        
        // createTime jest w sekundach, więc konwertujemy na milisekundy
        final long videoTimeMillis = video.getCreateTime() * 1000;
        
        // Sprawdzamy czy film jest z ostatnich 30 dni
        if (videoTimeMillis >= thirtyDaysAgo) {
          // Sprawdzamy czy opis zawiera odpowiednie hashtagi
          final String description = video.getDescription();
          if (description != null && 
              (description.toLowerCase().contains("#flamehub") || 
               description.toLowerCase().contains("#flamehubpl"))) {
            filteredVideos.add(video);
          }
        }
      }
      
      return filteredVideos;
    } catch (final IOException e) {
      throw new TikTokVideoFetchException(e.getMessage());
    }

  }

  public TikTokAccount fetchTikTokAccount(final String name) throws IOException {

    final Request request = new Request.Builder()
        .url("https://tiktok-api23.p.rapidapi.com/api/user/info?uniqueId=" + name)
        .get()
        .addHeader("x-rapidapi-key", "2e0613d978msh7a65897d638e4fdp15a618jsnbee06d86de01")
        .addHeader("x-rapidapi-host", "tiktok-api23.p.rapidapi.com")
        .build();

    final Response response = CLIENT.newCall(request).execute();
    try (final ResponseBody body = response.body()) {
      final String string = body.string();
      final TikTokAccountWrapper tikTokAccountWrapper = JsonUtil.GSON.fromJson(string,
          TikTokAccountWrapper.class);
      return tikTokAccountWrapper.getUserInfo();

    }

  }

}
