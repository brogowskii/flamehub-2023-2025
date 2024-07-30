package io.github.flamehub.tiktok;

final class TikTokVideo {

  private final int playCount;
  private final int diggCount;
  private final int commentCount;

  TikTokVideo(int playCount, int diggCount, int commentCount) {
    this.playCount = playCount;
    this.diggCount = diggCount;
    this.commentCount = commentCount;
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
    return "VideoStatistics{" +
        "playCount=" + playCount +
        ", diggCount=" + diggCount +
        ", commentCount=" + commentCount +
        '}';
  }
}