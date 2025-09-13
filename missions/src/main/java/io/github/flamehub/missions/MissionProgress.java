package io.github.flamehub.missions;

import dev.morphia.annotations.Entity;
import java.io.Serializable;

@Entity
public final class MissionProgress implements Serializable {

  private MissionType type;
  private long required;
  private int experience;
  private boolean claimed;
  private long progress;

  public MissionProgress() {
  }

  public MissionProgress(final MissionType type, final long required, final int experience) {
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

  public void setProgress(final long progress) {
    this.progress = progress;
  }

  public boolean isClaimed() {
    return claimed;
  }

  public void setClaimed(final boolean claimed) {
    this.claimed = claimed;
  }
}
