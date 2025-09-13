package io.github.flamehub.tiktok.video.verify;

public enum TikTokVideoVerifyFilter {
  ALL,
  ONLY_WAITING,
  ONLY_ACCEPTED,
  ONLY_BLOCKED;

  public TikTokVideoVerifyFilter next() {
    final int nextIndex = (ordinal() + 1) % values().length;
    return values()[nextIndex];
  }

}
