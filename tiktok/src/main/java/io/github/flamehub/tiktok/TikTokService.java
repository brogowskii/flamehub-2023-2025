package io.github.flamehub.tiktok;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import io.github.flamehub.commons.json.JsonUtil;
import io.github.flamehub.tiktok.account.TikTokAccount;
import io.github.flamehub.tiktok.video.TikTokVideoWrapper;
import io.github.flamehub.tiktok.video.TikTokVideosWrapper;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class TikTokService {

  private final static String API_KEY = "2e0613d978msh7a65897d638e4fdp15a618jsnbee06d86de01";
  private final static OkHttpClient CLIENT = new OkHttpClient();

  public static void main(String[] args) {
    TikTokService tikTokService = new TikTokService();
    CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
      try {
        final TikTokAccount tikTokAccount = tikTokService.fetchTikTokAccount("flamehub.pl");
        System.out.println(tikTokAccount);
        return tikTokAccount;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).thenAcceptAsync(tikTokAccount -> {
      final List<TikTokVideoWrapper> tikTokVideoWrappers;
      try {
        tikTokVideoWrappers = tikTokService.fetchVideos(
            tikTokAccount.getUser().getSecUid());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      System.out.println(tikTokVideoWrappers);
    });

    future.join();
  }

  public List<TikTokVideoWrapper> fetchVideos(String secUid) throws IOException {

    OkHttpClient client = new OkHttpClient();
    final String url =
        "https://tiktok-scraper2.p.rapidapi.com/user/videos?sec_uid=" + secUid + "&user_id=0";
    System.out.println(url);
    Request request = new Request.Builder()
        .url(url)
        .get()
        .addHeader("x-rapidapi-key", API_KEY)
        .addHeader("x-rapidapi-host", "tiktok-scraper2.p.rapidapi.com")
        .build();

    Response response = client.newCall(request).execute();
    try (final ResponseBody body = response.body()) {

      final String string = body.string();
      TikTokVideosWrapper tikTokVideosWrapper = JsonUtil.GSON.fromJson(string, TikTokVideosWrapper.class);

      return tikTokVideosWrapper.getPosts();

    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
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
