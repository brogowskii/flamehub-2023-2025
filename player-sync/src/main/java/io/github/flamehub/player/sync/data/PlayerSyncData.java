package io.github.flamehub.player.sync.data;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import java.util.UUID;

@Entity("player_sync_data")
public final class PlayerSyncData {

    @Id
    private UUID playerUniqueId;
    private String playerName;

    private String serializedInventory;
    private String serializedEnderchest;
    private String serializedPotionEffects;
    private String serializedLocation;

    private double health;

    private int foodLevel;
    private float saturation;
    private float exhaustion;

    private int totalExperience;
    private int expLevel;
    private float expProgress;

    private int heldItemSlot;
    private String gameMode;
    private boolean allowFlight;
    private boolean isFlying;

    private float walkSpeed;
    private float flySpeed;

    public PlayerSyncData(
            UUID playerUniqueId,
            String playerName, String serializedInventory,
            String serializedEnderchest, String serializedPotionEffects,
            String serializedLocation,
            double health,
            int foodLevel,
            float saturation,
            float exhaustion,
            int totalExperience,
            int expLevel,
            float expProgress,
            int heldItemSlot,
            String gameMode,
            boolean allowFlight,
            boolean isFlying,
            float walkSpeed,
            float flySpeed
    ) {
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.serializedInventory = serializedInventory;
        this.serializedEnderchest = serializedEnderchest;
        this.serializedPotionEffects = serializedPotionEffects;
        this.serializedLocation = serializedLocation;
        this.health = health;
        this.foodLevel = foodLevel;
        this.saturation = saturation;
        this.exhaustion = exhaustion;
        this.totalExperience = totalExperience;
        this.expLevel = expLevel;
        this.expProgress = expProgress;
        this.heldItemSlot = heldItemSlot;
        this.gameMode = gameMode;
        this.allowFlight = allowFlight;
        this.isFlying = isFlying;
        this.walkSpeed = walkSpeed;
        this.flySpeed = flySpeed;
    }

    public void setSerializedInventory(String serializedInventory) {
        this.serializedInventory = serializedInventory;
    }

    public void setSerializedEnderchest(String serializedEnderchest) {
        this.serializedEnderchest = serializedEnderchest;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public String getSerializedInventory() {
        return serializedInventory;
    }

    public String getSerializedPotionEffects() {
        return serializedPotionEffects;
    }

    public String getSerializedLocation() {
        return serializedLocation;
    }

    public double getHealth() {
        return health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public int getTotalExperience() {
        return totalExperience;
    }

    public int getExpLevel() {
        return expLevel;
    }

    public float getExpProgress() {
        return expProgress;
    }

    public int getHeldItemSlot() {
        return heldItemSlot;
    }

    public void setHeldItemSlot(int heldItemSlot) {
        this.heldItemSlot = heldItemSlot;
    }

    public String getGameMode() {
        return gameMode;
    }

    public boolean isAllowFlight() {
        return allowFlight;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    public String getSerializedEnderchest() {
        return serializedEnderchest;
    }

    public String getPlayerName() {
        return playerName;
    }
}
