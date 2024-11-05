package io.github.flamehub.tiktok.video;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public final class TikTokVideosWrapper {

  @SerializedName("itemList")
  private final List<TikTokVideoWrapper> posts;

  public TikTokVideosWrapper(final List<TikTokVideoWrapper> posts) {
    this.posts = posts;
  }

  public List<TikTokVideoWrapper> getPosts() {
    return posts;
  }
}
