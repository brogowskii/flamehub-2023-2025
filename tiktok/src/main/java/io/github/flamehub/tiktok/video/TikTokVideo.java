package io.github.flamehub.tiktok.video;

import com.google.gson.annotations.SerializedName;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity
public final class TikTokVideo {

  private String id;

  private String description;
  private int playCount;
  private int diggCount;
  private int commentCount;
  private long createTime;

  public TikTokVideo(final String id, final String description, final int playCount, final int diggCount,
      final int commentCount, final long createTime) {
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
