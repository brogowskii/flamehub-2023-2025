package io.github.flamehub.tiktok.video;

import com.google.gson.annotations.SerializedName;
import dev.morphia.annotations.Entity;

@Entity
public final class TikTokVideoWrapper {

  @SerializedName("desc")
  private final String description;
  @SerializedName("play")
  private final int playCount;
  @SerializedName("digg")
  private final int diggCount;
  @SerializedName("comment")
  private final int commentCount;

  public TikTokVideoWrapper(
      final String description,
      final int playCount,
      final int diggCount,
      final int commentCount) {
    this.description = description;
    this.playCount = playCount;
    this.diggCount = diggCount;
    this.commentCount = commentCount;
  }

  public String getDescription() {
    return description;
  }

  public int getPlayCount() {
    return playCount;
  }

  public int getDiggCount() {
    return diggCount;
  }

  public int getCommentCount() {
    return commentCount;
  }

  @Override
  public String toString() {
    return "TikTokVideo{" +
        "description='" + description + '\'' +
        ", playCount=" + playCount +
        ", diggCount=" + diggCount +
        ", commentCount=" + commentCount +
        '}';
  }
}