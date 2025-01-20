package io.github.flamehub.mines.mine;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import eu.decentsoftware.holograms.api.DHAPI;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.ChunkUtil;
import io.github.flamehub.commons.config.FlameConfigService;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Command(name = "mines")
@Permission("server.commands.mines")
public final class MineCommand {

  private final FlameConfigService flameConfigService;
  private final MineConfig mineConfig;
  private final Plugin plugin;

  WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager()
      .getPlugin("WorldEdit");

  public MineCommand(FlameConfigService flameConfigService, MineConfig mineConfig, Plugin plugin) {
    this.flameConfigService = flameConfigService;
    this.mineConfig = mineConfig;
    this.plugin = plugin;
  }

  public static Location calculateCenter(Location min, Location max) {

    int minX = Math.min(min.getBlockX(), max.getBlockX());
    int minY = Math.min(min.getBlockY(), max.getBlockY());
    int minZ = Math.min(min.getBlockZ(), max.getBlockZ());
    int maxX = Math.max(min.getBlockX(), max.getBlockX());
    int maxY = Math.max(min.getBlockY(), max.getBlockY());
    int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

    min.setX(minX);
    min.setY(minY);
    min.setZ(minZ);
    max.setX(maxX);
    max.setY(maxY);
    max.setZ(maxZ);

    double centerX = (min.getX() + max.getX()) / 2;
    double centerY = (min.getY() + max.getY()) / 2;
    double centerZ = (min.getZ() + max.getZ()) / 2;

    return new Location(min.getWorld(), centerX, centerY, centerZ).toCenterLocation();
  }

  @Execute(name = "create")
  void create(@Context Player player, @Arg String id, @Arg String delay,
      @Arg String materialsString) throws IncompleteRegionException {

    List<MineBlock> materials = new ArrayList<>();
    String[] split = materialsString.split(",");

    for (String material : split) {
      String[] split2 = material.split(":");
      materials.add(
          new MineBlock(Double.parseDouble(split2[1]), Material.valueOf(split2[0].toUpperCase())));
    }

    Region selection = worldEdit.getSession(player)
        .getSelection(worldEdit.getSession(player).getSelectionWorld()).clone();
    BlockVector3 minimumPoint = selection.getMinimumPoint();
    Location firstLocation = new Location(player.getWorld(), minimumPoint.getBlockX(),
        minimumPoint.getBlockY(), minimumPoint.getBlockZ()).clone();
    BlockVector3 maximumPoint = selection.getMaximumPoint();
    Location secondLocation = new Location(player.getWorld(), maximumPoint.getBlockX(),
        maximumPoint.getBlockY(), maximumPoint.getBlockZ()).clone();

    Mine mine = new Mine(id.toLowerCase(), delay, materials, firstLocation.clone(),
        secondLocation.clone());
    mine.createHolo();
    mine.regenerate();

    mineConfig.add(mine);
    flameConfigService.saveLocally(MineConfig.class);

    mineConfig.getMinesByLocation().clear();
    for (Mine generator : mineConfig.getMinesById().values()) {
      Location firstLoc2 = generator.getFirstLocation();
      Location secondLoc2 = generator.getSecondLocation();

      int minX = Math.min(firstLoc2.getBlockX(), secondLoc2.getBlockX());
      int maxX = Math.max(firstLoc2.getBlockX(), secondLoc2.getBlockX());
      int minY = Math.min(firstLoc2.getBlockY(), secondLoc2.getBlockY());
      int maxY = Math.max(firstLoc2.getBlockY(), secondLoc2.getBlockY());
      int minZ = Math.min(firstLoc2.getBlockZ(), secondLoc2.getBlockZ());
      int maxZ = Math.max(firstLoc2.getBlockZ(), secondLoc2.getBlockZ());

      for (int x = minX; x <= maxX; x++) {
        for (int y = minY; y <= maxY; y++) {
          for (int z = minZ; z <= maxZ; z++) {
            long l = ChunkUtil.coordinatesToLong(x, y, z);
            mineConfig.getMinesByLocation().put(l, generator);
          }
        }
      }
    }

    BukkitMessage.from("&aUtworzono mine: &7" + id).deliver(player);

  }

  @Execute(name = "reset")
  void reset(@Context Player player, @Arg Mine mine) {
    mine.regenerate();
    BukkitMessage.from("&aZresetowano mine: &7" + mine.getId()).deliver(player);
  }

  @Execute(name = "delete")
  void delete(@Context Player player, @Arg Mine mine) {

    DHAPI.removeHologram(mine.getId());
    mineConfig.remove(mine);
    flameConfigService.saveLocally(MineConfig.class);
    BukkitMessage.from("&aUsunięto mine: &7" + mine.getId()).deliver(player);
  }

  @Execute(name = "reload")
  void reload(@Context CommandSender sender) throws IllegalAccessException {
    mineConfig.getMinesByLocation().clear();
    flameConfigService.refreshLocally(MineConfig.class);

    for (Mine generator : mineConfig.getMinesById().values()) {
      Location firstLocation = generator.getFirstLocation();
      Location secondLocation = generator.getSecondLocation();

      int minX = Math.min(firstLocation.getBlockX(), secondLocation.getBlockX());
      int maxX = Math.max(firstLocation.getBlockX(), secondLocation.getBlockX());
      int minY = Math.min(firstLocation.getBlockY(), secondLocation.getBlockY());
      int maxY = Math.max(firstLocation.getBlockY(), secondLocation.getBlockY());
      int minZ = Math.min(firstLocation.getBlockZ(), secondLocation.getBlockZ());
      int maxZ = Math.max(firstLocation.getBlockZ(), secondLocation.getBlockZ());

      for (int x = minX; x <= maxX; x++) {
        for (int y = minY; y <= maxY; y++) {
          for (int z = minZ; z <= maxZ; z++) {
            long l = ChunkUtil.coordinatesToLong(x, y, z);
            mineConfig.getMinesByLocation().put(l, generator);
          }
        }
      }
    }

    BukkitMessage.from("&aPrzeładowano konfigurację.").deliver(sender);
  }

}
