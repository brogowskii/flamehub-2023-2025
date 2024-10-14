package io.github.flamehub.tiktok.video;

import java.util.List;

public final class TikTokVideosWrapper {

  private final List<TikTokVideoWrapper> posts;

  public TikTokVideosWrapper(final List<TikTokVideoWrapper> posts) {
    this.posts = posts;
  }

  public List<TikTokVideoWrapper> getPosts() {
    return posts;
  }
}
