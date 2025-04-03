package io.github.flamehub.tiktok.video;

import dev.morphia.annotations.Entity;

@Entity
public final class TikTokVideo {

  private final String id;

  private final String description;
  private final int playCount;
  private final int diggCount;
  private final int commentCount;
  private final long createTime;

  public TikTokVideo(
      final String id,
      final String description,
      final int playCount,
      final int diggCount,
      final int commentCount,
      final long createTime) {
    this.id = id;
    this.description = description;
    this.playCount = playCount;
    this.diggCount = diggCount;
    this.commentCount = commentCount;
    this.createTime = createTime;
  }

  public String getId() {
    return id;
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

  public long getCreateTime() {
    return createTime;
  }
}
