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

  public MineCommand(final FlameConfigService flameConfigService, final MineConfig mineConfig, final Plugin plugin) {
    this.flameConfigService = flameConfigService;
    this.mineConfig = mineConfig;
    this.plugin = plugin;
  }

  public static Location calculateCenter(final Location min, final Location max) {

    final int minX = Math.min(min.getBlockX(), max.getBlockX());
    final int minY = Math.min(min.getBlockY(), max.getBlockY());
    final int minZ = Math.min(min.getBlockZ(), max.getBlockZ());
    final int maxX = Math.max(min.getBlockX(), max.getBlockX());
    final int maxY = Math.max(min.getBlockY(), max.getBlockY());
    final int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

    min.setX(minX);
    min.setY(minY);
    min.setZ(minZ);
    max.setX(maxX);
    max.setY(maxY);
    max.setZ(maxZ);

    final double centerX = (min.getX() + max.getX()) / 2;
    final double centerY = (min.getY() + max.getY()) / 2;
    final double centerZ = (min.getZ() + max.getZ()) / 2;

    return new Location(min.getWorld(), centerX, centerY, centerZ).toCenterLocation();
  }

  @Execute(name = "create")
  void create(@Context final Player player, @Arg final String id, @Arg final String delay,
      @Arg final String materialsString) throws IncompleteRegionException {

    final List<MineBlock> materials = new ArrayList<>();
    final String[] split = materialsString.split(",");

    for (final String material : split) {
      final String[] split2 = material.split(":");
      materials.add(
          new MineBlock(Double.parseDouble(split2[1]), Material.valueOf(split2[0].toUpperCase())));
    }

    final Region selection = worldEdit.getSession(player)
        .getSelection(worldEdit.getSession(player).getSelectionWorld()).clone();
    final BlockVector3 minimumPoint = selection.getMinimumPoint();
    final Location firstLocation = new Location(player.getWorld(), minimumPoint.getBlockX(),
        minimumPoint.getBlockY(), minimumPoint.getBlockZ()).clone();
    final BlockVector3 maximumPoint = selection.getMaximumPoint();
    final Location secondLocation = new Location(player.getWorld(), maximumPoint.getBlockX(),
        maximumPoint.getBlockY(), maximumPoint.getBlockZ()).clone();

    final Mine mine = new Mine(id.toLowerCase(), delay, materials, firstLocation.clone(),
        secondLocation.clone());
    mine.createHolo();
    mine.regenerate();

    mineConfig.add(mine);
    flameConfigService.save(MineConfig.class);

    mineConfig.getMinesByLocation().clear();
    for (final Mine generator : mineConfig.getMinesById().values()) {
      final Location firstLoc2 = generator.getFirstLocation();
      final Location secondLoc2 = generator.getSecondLocation();

      final int minX = Math.min(firstLoc2.getBlockX(), secondLoc2.getBlockX());
      final int maxX = Math.max(firstLoc2.getBlockX(), secondLoc2.getBlockX());
      final int minY = Math.min(firstLoc2.getBlockY(), secondLoc2.getBlockY());
      final int maxY = Math.max(firstLoc2.getBlockY(), secondLoc2.getBlockY());
      final int minZ = Math.min(firstLoc2.getBlockZ(), secondLoc2.getBlockZ());
      final int maxZ = Math.max(firstLoc2.getBlockZ(), secondLoc2.getBlockZ());

      for (int x = minX; x <= maxX; x++) {
        for (int y = minY; y <= maxY; y++) {
          for (int z = minZ; z <= maxZ; z++) {
            final long l = ChunkUtil.coordinatesToLong(x, y, z);
            mineConfig.getMinesByLocation().put(l, generator);
          }
        }
      }
    }

    BukkitMessage.from("&aUtworzono mine: &7" + id).deliver(player);

  }

  @Execute(name = "reset")
  void reset(@Context final Player player, @Arg final Mine mine) {
    mine.regenerate();
    BukkitMessage.from("&aZresetowano mine: &7" + mine.getId()).deliver(player);
  }

  @Execute(name = "delete")
  void delete(@Context final Player player, @Arg final Mine mine) {

    DHAPI.removeHologram(mine.getId());
    mineConfig.remove(mine);
    flameConfigService.save(MineConfig.class);
    BukkitMessage.from("&aUsunięto mine: &7" + mine.getId()).deliver(player);
  }

  @Execute(name = "reload")
  void reload(@Context final CommandSender sender) throws IllegalAccessException {
    mineConfig.getMinesByLocation().clear();
    flameConfigService.refresh(MineConfig.class);

    for (final Mine generator : mineConfig.getMinesById().values()) {
      final Location firstLocation = generator.getFirstLocation();
      final Location secondLocation = generator.getSecondLocation();

      final int minX = Math.min(firstLocation.getBlockX(), secondLocation.getBlockX());
      final int maxX = Math.max(firstLocation.getBlockX(), secondLocation.getBlockX());
      final int minY = Math.min(firstLocation.getBlockY(), secondLocation.getBlockY());
      final int maxY = Math.max(firstLocation.getBlockY(), secondLocation.getBlockY());
      final int minZ = Math.min(firstLocation.getBlockZ(), secondLocation.getBlockZ());
      final int maxZ = Math.max(firstLocation.getBlockZ(), secondLocation.getBlockZ());

      for (int x = minX; x <= maxX; x++) {
        for (int y = minY; y <= maxY; y++) {
          for (int z = minZ; z <= maxZ; z++) {
            final long l = ChunkUtil.coordinatesToLong(x, y, z);
            mineConfig.getMinesByLocation().put(l, generator);
          }
        }
      }
    }

    BukkitMessage.from("&aPrzeładowano konfigurację.").deliver(sender);
  }

}
