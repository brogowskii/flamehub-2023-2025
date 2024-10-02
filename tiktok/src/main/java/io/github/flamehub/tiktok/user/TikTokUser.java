package io.github.flamehub.tiktok.user;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.tiktok.video.TikTokVideo;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity("tiktok_users")
public final class TikTokUser extends User {

  private String secUid;
  private String tikTokUsername;
  private String tikTokAccountURL;
  private Set<TikTokVideo> tikTokVideos = new HashSet<>();

  public TikTokUser() {
  }

  public TikTokUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public String getSecUid() {
    return secUid;
  }

  public String getTikTokUsername() {
    return tikTokUsername;
  }

  public String getTikTokAccountURL() {
    return tikTokAccountURL;
  }

  public Set<TikTokVideo> getTikTokVideos() {
    return tikTokVideos;
  }

  public void setSecUid(final String secUid) {
    this.secUid = secUid;
  }

  public void setTikTokUsername(final String tikTokUsername) {
    this.tikTokUsername = tikTokUsername;
  }

  public void setTikTokAccountURL(final String tikTokAccountURL) {
    this.tikTokAccountURL = tikTokAccountURL;
  }
}
