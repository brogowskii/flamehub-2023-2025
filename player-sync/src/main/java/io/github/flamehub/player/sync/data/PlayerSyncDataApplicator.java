package io.github.flamehub.player.sync.data;

import io.github.flamehub.commons.bukkit.util.LocationUtil;
import io.github.flamehub.commons.bukkit.util.SerializationUtil;
import io.github.flamehub.player.sync.util.PotionEffectSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class PlayerSyncDataApplicator {

  public static void apply(Player player, PlayerSyncData data, Location spawnLocation) {

    PlayerInventory inventory = player.getInventory();
    inventory.setContents(
        (ItemStack[]) SerializationUtil.deserializeBukkitObject(data.getSerializedInventory()));

    player.getEnderChest().setContents(
        (ItemStack[]) SerializationUtil.deserializeBukkitObject(data.getSerializedEnderchest()));

    PotionEffectSerializer.deserializePotionEffects(data.getSerializedPotionEffects())
        .forEach(player::addPotionEffect);

    Location deserialize = LocationUtil.deserialize(data.getSerializedLocation());
    if (!deserialize.getWorld().getName().equals("world")) {
      player.teleport(spawnLocation);
    } else {
      player.teleport(deserialize);
    }

    if (data.getHealth() > 0 && data.getHealth() < 20) {
      player.setHealth(data.getHealth());
    }

    player.setFoodLevel(data.getFoodLevel());
    player.setSaturation(data.getSaturation());
    player.setExhaustion(data.getExhaustion());

    player.setTotalExperience(data.getTotalExperience());
    player.setLevel(data.getExpLevel());
    player.setExp(data.getExpProgress());

    player.getInventory().setHeldItemSlot(data.getHeldItemSlot());
    if (player.hasPermission("server.essentials.commands.gamemode")) {
      player.setGameMode(GameMode.valueOf(data.getGameMode()));
    } else {
      player.setGameMode(GameMode.SURVIVAL);
    }

    if (player.hasPermission("server.essentials.commands.fly")) {
      player.setAllowFlight(data.isAllowFlight());
      player.setFlying(data.isFlying());
    } else {
      player.setAllowFlight(false);
      player.setFlying(false);
    }

    player.setWalkSpeed(data.getWalkSpeed());
    player.setFlySpeed(data.getFlySpeed());

    Bukkit.getPluginManager().callEvent(new PlayerDataLoadSyncEvent(player));
  }

}
