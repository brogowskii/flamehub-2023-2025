package io.github.flamehub.tiktok;

import io.github.flamehub.commons.json.JsonUtil;
import io.github.flamehub.tiktok.account.TikTokAccount;
import io.github.flamehub.tiktok.video.TikTokVideoFetchException;
import io.github.flamehub.tiktok.video.TikTokVideoWrapper;
import io.github.flamehub.tiktok.video.TikTokVideosWrapper;
import java.io.IOException;
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


//  public static void main(String[] args) {
//    TikTokService tikTokService = new TikTokService();
//    CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
//      try {
//        final TikTokAccount tikTokAccount = tikTokService.fetchTikTokAccount("flamehub.pl");
//        System.out.println(tikTokAccount);
//        return tikTokAccount;
//      } catch (IOException e) {
//        throw new RuntimeException(e);
//      }
//    }).thenAcceptAsync(tikTokAccount -> {
//      final List<TikTokVideoWrapper> tikTokVideoWrappers;
//      try {
//        tikTokVideoWrappers = tikTokService.fetchVideos(
//            tikTokAccount.getUser().getSecUid());
//      } catch (IOException e) {
//        throw new RuntimeException(e);
//      }
//      System.out.println(tikTokVideoWrappers);
//    });
//
//    future.join();
//  }

  public List<TikTokVideoWrapper> fetchVideos(String secUid)
      throws TikTokVideoFetchException, IOException {

    Request request = new Request.Builder()
        .url("https://tiktok-scraper2.p.rapidapi.com/user/videos_v2?sec_uid="+ secUid +"&count=30")
        .get()
        .addHeader("x-rapidapi-key", "2e0613d978msh7a65897d638e4fdp15a618jsnbee06d86de01")
        .addHeader("x-rapidapi-host", "tiktok-scraper2.p.rapidapi.com")
        .build();


    Response response = CLIENT.newCall(request).execute();

    if (!response.isSuccessful()) {
      throw new TikTokVideoFetchException("Failed to fetch videos");
    }

    try (final ResponseBody body = response.body()) {
      final String string = body.string();
      if (string.isEmpty()) {
        throw new TikTokVideoFetchException("Failed to fetch videos");
      }
      TikTokVideosWrapper tikTokVideosWrapper = JsonUtil.GSON.fromJson(string, TikTokVideosWrapper.class);
      return tikTokVideosWrapper.getPosts();
    } catch (IOException e) {
      throw new TikTokVideoFetchException(e.getMessage());
    }

  }

  public TikTokAccount fetchTikTokAccount(String name) throws IOException {

    Request request = new Request.Builder()
        .url("https://tiktok-scraper2.p.rapidapi.com/user/info?user_name=" + name)
        .get()
        .addHeader("x-rapidapi-key", API_KEY)
        .addHeader("x-rapidapi-host", "tiktok-scraper2.p.rapidapi.com")
        .build();

    Response response = CLIENT.newCall(request).execute();
    try (final ResponseBody body = response.body()) {
      final String string = body.string();
      return JsonUtil.GSON.fromJson(string, TikTokAccount.class);

    }

  }

}
