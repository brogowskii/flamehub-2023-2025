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

    this.warpFacade.add(warp);
    this.warpFacade.saveConfig(this.flameConfigService);

    BukkitMessage.from("&aPomyślnie stworzyłeś warp o nazwie: &2" + name).send(player);

  }

  @Execute(name = "setlocation")
  void create(@Context final Player player, @Arg String name) {

    name = name.toLowerCase();
    final Warp warp = this.warpFacade.find(name);
    if (warp == null) {
      BukkitMessage.from("&cTen warp nie istnieje!").send(player);
      return;
    }

    warp.setLocation(player.getLocation().clone().toCenterLocation());
    this.warpFacade.saveConfig(this.flameConfigService);

    BukkitMessage.from("&aPomyślnie ustawiłeś lokalizacje warp o nazwie &2" + name
            + " &aw miejscu w którym stoisz.")
        .send(player);

  }

  @Execute(name = "remove")
  void remove(@Context final Player player, @Arg String name) {
    name = name.toLowerCase();
    final Warp warp = this.warpFacade.find(name);
    if (warp == null) {
      BukkitMessage.from("&cTen warp nie istnieje!").send(player);
      return;
    }

    this.warpFacade.remove(warp);
    this.warpFacade.saveConfig(this.flameConfigService);

    BukkitMessage.from("&aPomyślnie usunąłeś warp o nazwie: &2" + name).send(player);

  }

  @Execute(name = "reload")
  void remove(@Context final CommandSender sender) {

    try {
      this.warpFacade.refreshConfig(this.flameConfigService);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    BukkitMessage.from("&aPomyślnie przeładowano plik konfiguracyjny!").send(sender);
  }

  @Execute(name = "tp")
  void tp(@Context final CommandSender sender, @Arg final Player player, @Arg final String name) {
    final Warp warp = this.warpFacade.find(name);
    if (warp == null) {
      BukkitMessage.from("&cTen warp nie istnieje!").send(player);
      return;
    }

    player.teleport(warp.getLocation().clone());
  }
}
