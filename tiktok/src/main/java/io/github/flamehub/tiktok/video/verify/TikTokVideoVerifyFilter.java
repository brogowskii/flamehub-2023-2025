package io.github.flamehub.tiktok.video.verify;

public enum TikTokVideoVerifyFilter {
  ALL,
  ONLY_WAITING,
  ONLY_ACCEPTED,
  ONLY_BLOCKED;

  public TikTokVideoVerifyFilter next() {
    int nextIndex = (this.ordinal() + 1) % values().length;
    return values()[nextIndex];
  }

}
