package io.github.flamehub.tiktok.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.UserUpdatable;
import io.github.flamehub.tiktok.video.TikTokVideo;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity("tiktok_users")
public final class TikTokUser extends UserUpdatable {

  private String secUid;
  private String tikTokUsername;
  private String tikTokAccountURL;
  private final Set<TikTokVideo> tikTokVideos = new HashSet<>();
  private final Set<String> claimedVideos = new HashSet<>();

  private long lastRefreshedTime;

  public TikTokUser() {
  }

  public TikTokUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public String getSecUid() {
    return secUid;
  }

  public void setSecUid(final String secUid) {
    this.secUid = secUid;
  }

  public String getTikTokUsername() {
    return tikTokUsername;
  }

  public void setTikTokUsername(final String tikTokUsername) {
    this.tikTokUsername = tikTokUsername;
  }

  public String getTikTokAccountURL() {
    return tikTokAccountURL;
  }

  public void setTikTokAccountURL(final String tikTokAccountURL) {
    this.tikTokAccountURL = tikTokAccountURL;
  }

  public Set<TikTokVideo> getTikTokVideos() {
    return tikTokVideos;
  }

  public long getLastRefreshedTime() {
    return lastRefreshedTime;
  }

  public void setLastRefreshedTime(final long lastRefreshedTime) {
    this.lastRefreshedTime = lastRefreshedTime;
  }

  public Set<String> getClaimedVideos() {
    return claimedVideos;
  }

  public void refreshTikTokVideos(Collection<TikTokVideo> tikTokVideos) {
    this.tikTokVideos.clear();
    this.tikTokVideos.addAll(tikTokVideos);

  }

}
