package io.github.flamehub.achievements.achievement;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public final class AchievementAction implements Serializable {
    private AchievementActionType actionType;
    private Material material;
    private Action action;

    public AchievementAction() {
    }

    public AchievementAction(AchievementActionType actionType, @Nullable Material material, @Nullable Action action) {
        this.actionType = actionType;
        this.material = material;
        this.action = action;
    }

    public AchievementActionType getActionType() {
        return actionType;
    }

    public void setActionType(AchievementActionType actionType) {
        this.actionType = actionType;
    }

    @Nullable
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(@Nullable Material material) {
        this.material = material;
    }

    @Nullable
    public Action getAction() {
        return action;
    }

    public void setAction(@Nullable Action action) {
        this.action = action;
    }

}