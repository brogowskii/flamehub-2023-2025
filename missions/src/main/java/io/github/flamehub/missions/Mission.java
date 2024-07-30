package io.github.flamehub.missions;

import dev.morphia.annotations.Entity;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Entity
public final class Mission implements Serializable {

  private MissionType type;
  private long required;
  private int shards;
  private long expiration;
  private boolean claimed;
  private long progress;

  public Mission() {
  }

  public Mission(MissionType type, long required, int shards) {
    this.type = type;
    this.required = required;
    this.shards = shards;
    this.expiration = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24);
  }

  public MissionType getType() {
    return type;
  }

  public long getRequired() {
    return required;
  }

  public int getShards() {
    return shards;
  }

  public long getProgress() {
    return progress;
  }

  public void setProgress(long progress) {
    this.progress = progress;
  }

  public long getExpiration() {
    return expiration;
  }

  public boolean isClaimed() {
    return claimed;
  }

  public void setClaimed(boolean claimed) {
    this.claimed = claimed;
  }
}
