package io.github.flamehub.spoof.tool;

import com.halos.spoofer.api.spigot.SpigotSpooferAPI;
import com.halos.spoofer.api.spigot.event.FakePlayerCreatedEvent;
import com.halos.spoofer.api.spigot.event.FakePlayerDestroyEvent;
import com.halos.spoofer.api.spigot.event.FakePlayerLoginEvent;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class SpoofToolListener implements Listener {

  private final SpigotSpooferAPI spooferAPI = SpigotSpooferAPI.get();
  private final NetworkPlayerCache networkPlayerCache;
  private final NetworkServerCache networkServerCache;

  private final Location minLocation = new Location(Bukkit.getWorld("world"), -27.5, 97, 4.5);
  private final Location maxLocation = new Location(Bukkit.getWorld("world"), -44.5, 98, -9.5);

  public SpoofToolListener(final NetworkPlayerCache networkPlayerCache,
      final NetworkServerCache networkServerCache) {
    this.networkPlayerCache = networkPlayerCache;
    this.networkServerCache = networkServerCache;
  }

  @EventHandler
  public void onJoin(FakePlayerCreatedEvent event) {
    final Player player = event.player();

    final NetworkPlayer networkPlayer = new NetworkPlayer(player.getUniqueId(), player.getName());
    final NetworkServer current = networkServerCache.getCurrent();
    networkPlayer.setServer(current.getName());
    networkPlayer.setProxy("null");
    networkPlayer.setServerCategory(current.getCategory());

    CompletableFuture.runAsync(() -> networkPlayerCache.save(networkPlayer));

    final Location validLocation = getValidLocation();
    validLocation.setY(98);
    final Location centerLocation = validLocation.toCenterLocation();
    centerLocation.subtract(0, 0.5, 0);

    centerLocation.setYaw(ThreadLocalRandom.current().nextInt(-180, 180));
    centerLocation.setPitch(ThreadLocalRandom.current().nextInt(-90, 90));

    player.teleport(centerLocation);

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
    Random random = new Random();
    Location location;

    do {
      double x = minLocation.getX() + random.nextDouble() * (maxLocation.getX() - minLocation.getX());
      double z = minLocation.getZ() + random.nextDouble() * (maxLocation.getZ() - minLocation.getZ());

      location = findGroundBlock(new Location(minLocation.getWorld(), x, maxLocation.getY(), z));
    } while (location == null || !isInside(location));

    return location;
  }

  private Location findGroundBlock(Location startLocation) {
    World world = startLocation.getWorld();

    for (int y = (int) startLocation.getY(); y >= minLocation.getBlockY(); y--) {
      Location loc = new Location(world, startLocation.getX(), y, startLocation.getZ());

      if (loc.getBlock().getType() == Material.MAGENTA_CONCRETE_POWDER) {
        return loc.getBlock().getLocation();
      }
    }

    return null;
  }


  public boolean isInside(Location location) {
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
