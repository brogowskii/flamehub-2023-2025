package io.github.flamehub.tiktok.video;

import java.util.List;

public class TikTokVideos {

  private final List<TikTokVideo> posts;

  public TikTokVideos(final List<TikTokVideo> posts) {
    this.posts = posts;
  }

  public List<TikTokVideo> getPosts() {
    return posts;
  }
}
