package io.github.flamehub.tiktok.video.verify;

public enum TikTokVideoVerifySort {

  NONE,
  VPLN,
  NEWEST,
  OLDEST;

  public TikTokVideoVerifySort next() {
    final int nextIndex = (ordinal() + 1) % values().length;
    return values()[nextIndex];
  }
}
