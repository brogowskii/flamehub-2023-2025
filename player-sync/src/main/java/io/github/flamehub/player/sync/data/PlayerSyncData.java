package io.github.flamehub.player.sync.data;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.UUID;

@Entity("player_sync_data")
public final class PlayerSyncData {

  @Id
  private final UUID playerUniqueId;
  private final String playerName;
  private final String serializedPotionEffects;
  private final String serializedLocation;
  private final double health;
  private final int foodLevel;
  private final float saturation;
  private final float exhaustion;
  private final int totalExperience;
  private final int expLevel;
  private final float expProgress;
  private final String gameMode;
  private final boolean allowFlight;
  private final boolean isFlying;
  private final float walkSpeed;
  private final float flySpeed;
  private String serializedInventory;
  private String serializedEnderchest;
  private int heldItemSlot;

  public PlayerSyncData(
      final UUID playerUniqueId,
      final String playerName,
      final String serializedInventory,
      final String serializedEnderchest,
      final String serializedPotionEffects,
      final String serializedLocation,
      final double health,
      final int foodLevel,
      final float saturation,
      final float exhaustion,
      final int totalExperience,
      final int expLevel,
      final float expProgress,
      final int heldItemSlot,
      final String gameMode,
      final boolean allowFlight,
      final boolean isFlying,
      final float walkSpeed,
      final float flySpeed
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

  public UUID getPlayerUniqueId() {
    return playerUniqueId;
  }

  public String getSerializedInventory() {
    return serializedInventory;
  }

  public void setSerializedInventory(final String serializedInventory) {
    this.serializedInventory = serializedInventory;
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

  public void setHeldItemSlot(final int heldItemSlot) {
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

  public void setSerializedEnderchest(final String serializedEnderchest) {
    this.serializedEnderchest = serializedEnderchest;
  }

  public String getPlayerName() {
    return playerName;
  }
}
