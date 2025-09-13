package io.github.flamehub.achievements.achievement;

import java.io.Serializable;
import org.bukkit.Material;

public final class AchievementCategory implements Serializable {

  private String id;
  private AchievementAction action;
  private String friendlyName;
  private Material icon;
  private int slot;

  public AchievementCategory() {
  }

  public AchievementCategory(
      final String id,
      final AchievementAction action,
      final String friendlyName,
      final Material icon,
      final int slot
  ) {
    this.id = id;
    this.action = action;
    this.friendlyName = friendlyName;
    this.icon = icon;
    this.slot = slot;
  }

  public String getId() {
    return id;
  }

  public AchievementAction getAction() {
    return action;
  }

  public String getFriendlyName() {
    return friendlyName;
  }

  public Material getIcon() {
    return icon;
  }

  public int getSlot() {
    return slot;
  }

}