package io.github.flamehub.tiktok;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import io.github.flamehub.commons.json.JsonUtil;
import io.github.flamehub.tiktok.account.TikTokAccount;
import io.github.flamehub.tiktok.video.TikTokVideo;
import io.github.flamehub.tiktok.video.TikTokVideos;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class TikTokService {

  private final static String API_KEY = "2e0613d978msh7a65897d638e4fdp15a618jsnbee06d86de01";
  private final static OkHttpClient CLIENT = new OkHttpClient();

  public List<TikTokVideo> fetchVideos(String secUid) throws IOException {

    OkHttpClient client = new OkHttpClient();
    final String url =
        "https://tiktok-scraper2.p.rapidapi.com/user/videos?sec_uid="+ secUid +"&user_id=0";
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
      TikTokVideos tikTokVideos = JsonUtil.GSON.fromJson(string, TikTokVideos.class);

      return tikTokVideos.getPosts();

    }
    catch (IOException e) {
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
      final List<TikTokVideo> tikTokVideos;
      try {
        tikTokVideos = tikTokService.fetchVideos(
            tikTokAccount.getUser().getSecUid());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      System.out.println(tikTokVideos);
    });

    future.join();
  }

}
