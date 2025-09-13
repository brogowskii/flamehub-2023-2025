package io.github.flamehub.achievements.achievement;

import java.io.Serializable;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.Nullable;

public final class AchievementAction implements Serializable {

  private AchievementActionType actionType;
  private List<Material> material;
  private Action action;

  public AchievementAction() {
  }

  public AchievementAction(
      final AchievementActionType actionType,
      @Nullable final List<Material> material,
      @Nullable final Action action
  ) {
    this.actionType = actionType;
    this.material = material;
    this.action = action;
  }

  public AchievementActionType getActionType() {
    return actionType;
  }

  public void setActionType(final AchievementActionType actionType) {
    this.actionType = actionType;
  }

  @Nullable
  public List<Material> getMaterial() {
    return material;
  }

  @Nullable
  public Action getAction() {
    return action;
  }

  public void setAction(@Nullable final Action action) {
    this.action = action;
  }

}