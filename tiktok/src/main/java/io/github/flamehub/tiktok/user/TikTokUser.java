package io.github.flamehub.tiktok.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.UserUpdatable;
import io.github.flamehub.tiktok.video.TikTokVideo;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity("tiktok_users")
public final class TikTokUser extends UserUpdatable {

  private final Map<String, Integer> claimedPointsVideos = new HashMap<>();
  private final Set<TikTokVideo> tikTokVideos = new HashSet<>();
  private String secUid;
  private String tikTokUsername;
  private String tikTokAccountURL;
  private double earnedMoney;
  private int points;
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

  public double getEarnedMoney() {
    return earnedMoney;
  }

  public void setEarnedMoney(final double earnedMoney) {
    this.earnedMoney = earnedMoney;
  }

  public void addEarnedMoney(final double earnedMoney) {
    this.earnedMoney += earnedMoney;
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

  public void refreshTikTokVideos(Collection<TikTokVideo> tikTokVideos) {
    this.tikTokVideos.clear();
    this.tikTokVideos.addAll(tikTokVideos);

  }

  public Map<String, Integer> getClaimedPointsVideos() {
    return claimedPointsVideos;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(final int points) {
    this.points = points;
  }

  public void addPoints(final int points) {
    this.points += points;
  }

  public void removePoints(final int points) {
    this.points -= points;
  }
}
