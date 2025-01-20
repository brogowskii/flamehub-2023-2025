package io.github.flamehub.essentials.warp;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "warpadmin")
@Permission("server.essentials.commands.warpadmin")
final class WarpCommandAdmin {

  private final FlameConfigService flameConfigService;
  private final WarpFacade warpFacade;

  WarpCommandAdmin(final WarpFacade warpFacade, final FlameConfigService flameConfigService) {
    this.flameConfigService = flameConfigService;
    this.warpFacade = warpFacade;
  }

  @Execute(name = "create")
  void create(
      @Context final Player player,
      @Arg final String name,
      @Arg final String icon,
      @Arg final int slot,
      @Join final String guiName
  ) {

    final Location location = player.getLocation();
    final Location clone = location.clone().toBlockLocation();
    final Warp warp = new Warp(name, guiName, Material.valueOf(icon), slot, clone);

    warpFacade.add(warp);
    warpFacade.saveConfig(flameConfigService);

    BukkitMessage.from("&aPomyślnie stworzyłeś warp o nazwie: &2" + name).deliver(player);

  }

  @Execute(name = "setlocation")
  void create(@Context final Player player, @Arg String name) {

    name = name.toLowerCase();
    final Warp warp = warpFacade.find(name);
    if (warp == null) {
      BukkitMessage.from("&cTen warp nie istnieje!").deliver(player);
      return;
    }

    warp.setLocation(player.getLocation().clone().toCenterLocation());
    warpFacade.saveConfig(flameConfigService);

    BukkitMessage.from("&aPomyślnie ustawiłeś lokalizacje warp o nazwie &2" + name
            + " &aw miejscu w którym stoisz.")
        .deliver(player);

  }

  @Execute(name = "remove")
  void remove(@Context final Player player, @Arg String name) {
    name = name.toLowerCase();
    final Warp warp = warpFacade.find(name);
    if (warp == null) {
      BukkitMessage.from("&cTen warp nie istnieje!").deliver(player);
      return;
    }

    warpFacade.remove(warp);
    warpFacade.saveConfig(flameConfigService);

    BukkitMessage.from("&aPomyślnie usunąłeś warp o nazwie: &2" + name).deliver(player);

  }

  @Execute(name = "reload")
  void remove(@Context final CommandSender sender) {

    try {
      warpFacade.refreshConfig(flameConfigService);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    BukkitMessage.from("&aPomyślnie przeładowano plik konfiguracyjny!").deliver(sender);
  }

  @Execute(name = "tp")
  void tp(@Context final CommandSender sender, @Arg final Player player, @Arg final String name) {
    final Warp warp = warpFacade.find(name);
    if (warp == null) {
      BukkitMessage.from("&cTen warp nie istnieje!").deliver(player);
      return;
    }

    player.teleport(warp.getLocation().clone());
  }
}
