package io.github.flamehub.achievements.achievement;

import org.bukkit.Material;

import java.io.Serializable;

public class AchievementCategory implements Serializable {
    private String id;
    private AchievementAction action;
    private String friendlyName;
    private Material icon;
    private int slot;

    public AchievementCategory() {
    }

    public AchievementCategory(String id, AchievementAction action, String friendlyName, Material icon, int slot) {
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