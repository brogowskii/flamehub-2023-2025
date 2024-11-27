package io.github.flamehub.tiktok.video;

import com.google.gson.annotations.SerializedName;

public final class TikTokVideoData {

  @SerializedName("data")
  private final TikTokVideosWrapper tikTokVideosWrapper;

  public TikTokVideoData(final TikTokVideosWrapper tikTokVideosWrapper) {
    this.tikTokVideosWrapper = tikTokVideosWrapper;
  }

  public TikTokVideosWrapper getTikTokVideosWrapper() {
    return tikTokVideosWrapper;
  }
}
