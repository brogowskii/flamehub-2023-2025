package io.github.flamehub.achievements.achievement;

import java.io.Serializable;

public final class AchievementReward implements Serializable {

  private String friendlyName;
  private String command;

  public AchievementReward(final String friendlyName, final String command) {
    this.friendlyName = friendlyName;
    this.command = command;
  }

  public AchievementReward() {
  }

  public String getFriendlyName() {
    return friendlyName;
  }

  public String getCommand() {
    return command;
  }
}
