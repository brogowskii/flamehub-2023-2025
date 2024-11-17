package io.github.flamehub.tiktok.account;

public final class TikTokAccountWrapper {

  private final TikTokAccount userInfo;

  public TikTokAccountWrapper(final TikTokAccount userInfo) {
    this.userInfo = userInfo;
  }

  public TikTokAccount getUserInfo() {
    return userInfo;
  }
}
