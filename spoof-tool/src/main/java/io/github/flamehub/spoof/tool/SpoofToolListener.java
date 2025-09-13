package io.github.flamehub.spoof.tool;

import com.halos.spoofer.api.spigot.SpigotSpooferAPI;
import com.halos.spoofer.api.spigot.event.FakePlayerCreatedEvent;
import com.halos.spoofer.api.spigot.event.FakePlayerDestroyEvent;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.commons.util.RandomUtil;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class SpoofToolListener implements Listener {

  private final SpigotSpooferAPI spooferAPI = SpigotSpooferAPI.get();
  private final NetworkPlayerCache networkPlayerCache;
  private final NetworkServerFacade networkServerFacade;

  private final SpoofToolConfig spoofToolConfig;

  public SpoofToolListener(
      final NetworkPlayerCache networkPlayerCache,
      final NetworkServerFacade networkServerFacade,
      final SpoofToolConfig spoofToolConfig) {
    this.networkPlayerCache = networkPlayerCache;
    this.networkServerFacade = networkServerFacade;
    this.spoofToolConfig = spoofToolConfig;
  }

  @EventHandler
  public void onJoin(FakePlayerCreatedEvent event) {
    final Player player = event.player();

    final NetworkPlayer networkPlayer = new NetworkPlayer(player.getUniqueId(), player.getName());
    final NetworkServer current = networkServerFacade.getCurrent();
    networkPlayer.setServer(current.getName());
    networkPlayer.setProxy("null");
    networkPlayer.setServerCategory(current.getCategory());

    CompletableFuture.runAsync(() -> networkPlayerCache.save(networkPlayer));

    if (RandomUtil.getChance(30)) {
      CommonsPlugin.getInstance().getFlameDispatcher().dispatchLater(() -> player.performCommand("incognito"), 100L);
    }

//    final Location validLocation = getValidLocation();
//    final Location centerLocation = validLocation.toCenterLocation();
//
//    centerLocation.setYaw(ThreadLocalRandom.current().nextInt(-180, 180));
//    centerLocation.setPitch(ThreadLocalRandom.current().nextInt(-90, 90));
//    centerLocation.setY(centerLocation.getBlockY() + 1.0);

    player.getInventory().clear();
//    player.teleport(centerLocation);

  }

  @EventHandler
  public void onQuit(FakePlayerDestroyEvent event) {
    final Player player = event.player();
    final NetworkPlayer networkPlayer = networkPlayerCache.findByUniqueId(player.getUniqueId());
    if (networkPlayer == null) {
      return;
    }

    CompletableFuture.runAsync(() -> networkPlayerCache.delete(networkPlayer));
  }

  private Location getValidLocation() {
    final Location minLocation = spoofToolConfig.getMinLocation();
    final Location maxLocation = spoofToolConfig.getMaxLocation();
    final World world = minLocation.getWorld();

    if (world == null) {
      throw new IllegalStateException("World cannot be null for minLocation or maxLocation.");
    }

    final int minX = Math.min(minLocation.getBlockX(), maxLocation.getBlockX());
    final int maxX = Math.max(minLocation.getBlockX(), maxLocation.getBlockX());
    final int minY = Math.min(minLocation.getBlockY(), maxLocation.getBlockY());
    final int maxY = Math.max(minLocation.getBlockY(), maxLocation.getBlockY());
    final int minZ = Math.min(minLocation.getBlockZ(), maxLocation.getBlockZ());
    final int maxZ = Math.max(minLocation.getBlockZ(), maxLocation.getBlockZ());

    Location randomLocation;
    int attempts = 0;

    do {
      if (attempts++ > 100) {
        throw new IllegalStateException("Unable to find a valid location within the cuboid after 100 attempts.");
      }

      int randomX = ThreadLocalRandom.current().nextInt(minX, maxX + 1);
      int randomY = ThreadLocalRandom.current().nextInt(minY, maxY + 1);
      int randomZ = ThreadLocalRandom.current().nextInt(minZ, maxZ + 1);

      randomLocation = new Location(world, randomX, randomY, randomZ);

    } while (!isValidBlock(randomLocation));

    return randomLocation;
  }

  private boolean isValidBlock(Location location) {
    World world = location.getWorld();
    if (world == null) {
      return false;
    }

    return world.getBlockAt(location).getType().isSolid()
        && world.getBlockAt(location.clone().add(0, 1, 0)).getType() == Material.AIR
        && world.getBlockAt(location.clone().add(0, 2, 0)).getType() == Material.AIR;
  }

  public boolean isInside(Location location) {
    final Location maxLocation = spoofToolConfig.getMaxLocation();
    final Location minLocation = spoofToolConfig.getMinLocation();
    if (!location.getWorld().getName().equals(minLocation.getWorld().getName())) {
      return false;
    }

    int minX = Math.min(minLocation.getBlockX(), maxLocation.getBlockX());
    int maxX = Math.max(minLocation.getBlockX(), maxLocation.getBlockX());
    int minY = Math.min(minLocation.getBlockY(), maxLocation.getBlockY());
    int maxY = Math.max(minLocation.getBlockY(), maxLocation.getBlockY());
    int minZ = Math.min(minLocation.getBlockZ(), maxLocation.getBlockZ());
    int maxZ = Math.max(minLocation.getBlockZ(), maxLocation.getBlockZ());

    return ((location.getY() < maxY) && (location.getY() >= minY))
        && location.getBlockX() > minX
        && location.getBlockX() < maxX
        && location.getBlockZ() > minZ
        && location.getBlockZ() < maxZ;
  }

}
