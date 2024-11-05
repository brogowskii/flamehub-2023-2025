package io.github.flamehub.tiktok.video;

public final class TikTokVideoStats {

  private int playCount;
  private int diggCount;
  private int commentCount;

  public int getPlayCount() {
    return playCount;
  }

  public void setPlayCount(final int playCount) {
    this.playCount = playCount;
  }

  public int getDiggCount() {
    return diggCount;
  }

  public void setDiggCount(final int diggCount) {
    this.diggCount = diggCount;
  }

  public int getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(final int commentCount) {
    this.commentCount = commentCount;
  }
}
