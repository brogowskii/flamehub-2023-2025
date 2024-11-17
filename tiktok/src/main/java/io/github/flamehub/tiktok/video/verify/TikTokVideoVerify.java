package io.github.flamehub.tiktok.video.verify;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.time.Instant;

@Entity
public final class TikTokVideoVerify {

  @Id
  private final String id;
  private final Instant createTime = Instant.now();
  private final String playerName;
  private final String tikTokAccountURL;
  private final String tikTokAccountUsername;
  private final String description;
  private final int playCount;
  private final int diggCount;
  private final int commentCount;
  private TikTokVideoVerifyStatus status = TikTokVideoVerifyStatus.WAITING;

  public TikTokVideoVerify(final String playerName,
      final String tikTokAccountURL,
      final String tikTokAccountUsername, final String id, final String description,
      final int playCount, final int diggCount,
      final int commentCount) {
    this.playerName = playerName;
    this.tikTokAccountURL = tikTokAccountURL;
    this.tikTokAccountUsername = tikTokAccountUsername;
    this.id = id;
    this.description = description;
    this.playCount = playCount;
    this.diggCount = diggCount;
    this.commentCount = commentCount;
  }

  public Instant getCreateTime() {
    return createTime;
  }

  public String getPlayerName() {
    return playerName;
  }

  public String getTikTokAccountURL() {
    return tikTokAccountURL;
  }

  public String getTikTokAccountUsername() {
    return tikTokAccountUsername;
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

  public TikTokVideoVerifyStatus getStatus() {
    return status;
  }

  public void setStatus(final TikTokVideoVerifyStatus status) {
    this.status = status;
  }
}
