package io.github.flamehub.missions;

import dev.morphia.annotations.Entity;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Entity
public final class MissionProgress implements Serializable {

  private MissionType type;
  private long required;
  private int experience;
  private boolean claimed;
  private long progress;

  public MissionProgress() {
  }

  public MissionProgress(MissionType type, long required, int experience) {
    this.type = type;
    this.required = required;
    this.experience = experience;
  }

  public MissionType getType() {
    return type;
  }

  public long getRequired() {
    return required;
  }

  public int getExperience() {
    return experience;
  }

  public long getProgress() {
    return progress;
  }

  public void setProgress(long progress) {
    this.progress = progress;
  }

  public boolean isClaimed() {
    return claimed;
  }

  public void setClaimed(boolean claimed) {
    this.claimed = claimed;
  }
}
