package io.github.flamehub.tiktok.account;

public final class TikTokAccount {

  private final TikTokAccountStats stats;
  private final TikTokAccountInfo user;

  public TikTokAccount(final TikTokAccountStats stats, final TikTokAccountInfo user) {
    this.stats = stats;
    this.user = user;
  }

  public TikTokAccountStats getStats() {
    return stats;
  }

  public TikTokAccountInfo getUser() {
    return user;
  }

  @Override
  public String toString() {
    return "TikTokAccountWrapper{" +
        "stats=" + stats.toString() +
        ", user=" + user.toString() +
        '}';
  }
}
