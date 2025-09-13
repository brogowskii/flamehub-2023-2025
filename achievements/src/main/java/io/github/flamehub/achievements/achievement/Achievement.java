package io.github.flamehub.achievements.achievement;

import java.io.Serializable;
import java.util.List;

public final class Achievement implements Serializable {

  private int id;
  private String category;
  private List<AchievementReward> rewards;
  private long required;

  public Achievement() {
  }

  public Achievement(final int id, final String category, final List<AchievementReward> rewards, final long required) {
    this.id = id;
    this.category = category;
    this.rewards = rewards;
    this.required = required;
  }

  public int getId() {
    return id;
  }

  public String getCategory() {
    return category;
  }

  public List<AchievementReward> getRewards() {
    return rewards;
  }

  public long getRequired() {
    return required;
  }
}
