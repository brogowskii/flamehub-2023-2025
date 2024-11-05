package io.github.flamehub.tiktok.video;

import com.google.gson.annotations.SerializedName;
import dev.morphia.annotations.Entity;

public final class TikTokVideoWrapper {

  private final String id;

  @SerializedName("desc")
  private final String description;

  private final TikTokVideoStats stats;
  private final long createTime;


  public TikTokVideoWrapper(final String id, final String description, final TikTokVideoStats stats,
      final long createTime) {
    this.id = id;
    this.description = description;
    this.stats = stats;
    this.createTime = createTime;
  }

  public String getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public TikTokVideoStats getStats() {
    return stats;
  }

  public long getCreateTime() {
    return createTime;
  }

  public TikTokVideo unwrap() {
    return new TikTokVideo(id, description, stats.getPlayCount(), stats.getDiggCount(), stats.getCommentCount(), createTime);
  }

  @Override
  public String toString() {
    return "TikTokVideoWrapper{" +
        "id='" + id + '\'' +
        ", description='" + description + '\'' +
        ", stats=" + stats +
        ", createTime=" + createTime +
        '}';
  }
}