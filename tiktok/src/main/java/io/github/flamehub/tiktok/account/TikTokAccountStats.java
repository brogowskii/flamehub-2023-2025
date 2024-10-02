package io.github.flamehub.tiktok.account;

public final class TikTokAccountStats {

  private final int diggCount;
  private final int followerCount;
  private final int videoCount;
  private final int heartCount;

  TikTokAccountStats(final int diggCount, final int followerCount, final int videoCount,
      final int heartCount) {
    this.diggCount = diggCount;
    this.followerCount = followerCount;
    this.videoCount = videoCount;
    this.heartCount = heartCount;
  }

  public int getDiggCount() {
    return diggCount;
  }

  public int getFollowerCount() {
    return followerCount;
  }

  public int getVideoCount() {
    return videoCount;
  }

  public int getHeartCount() {
    return heartCount;
  }

  @Override
  public String toString() {
    return "TikTokAccountStats{" +
        "diggCount=" + diggCount +
        ", followerCount=" + followerCount +
        ", videoCount=" + videoCount +
        ", heartCount=" + heartCount +
        '}';
  }
}
