package io.github.flamehub.player.sync.data;

import io.github.flamehub.commons.bukkit.util.LocationUtil;
import io.github.flamehub.commons.bukkit.util.SerializationUtil;
import io.github.flamehub.player.sync.util.PotionEffectSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class PlayerSyncDataFactory {

    public static PlayerSyncData create(Player player) {



        return create(player, player.getLocation());
    }

    public static PlayerSyncData create(Player player, Location location) {
        return new PlayerSyncData(
                player.getUniqueId(),
                player.getName(),
                SerializationUtil.serializeBukkitObject(player.getInventory().getContents()),
                SerializationUtil.serializeBukkitObject(player.getEnderChest().getContents()),
                PotionEffectSerializer.serializePotionEffects(player.getActivePotionEffects()),
                LocationUtil.serialize(location.clone()),
                player.getHealth(),
                player.getFoodLevel(),
                player.getSaturation(),
                player.getExhaustion(),
                player.getTotalExperience(),
                player.getLevel(),
                player.getExp(),
                player.getInventory().getHeldItemSlot(),
                player.getGameMode().toString(),
                player.getAllowFlight(),
                player.isFlying(),
                player.getWalkSpeed(),
                player.getFlySpeed()
        );
    }

}
