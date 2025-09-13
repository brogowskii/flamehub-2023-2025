package io.github.flamehub.spoof.tool;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Command(name = "spc")
@Permission("server.commands.spc")
public final class SpoofToolCommand {

  private final SpoofToolConfig config;
  private final FlameConfigService flameConfigService;

  public SpoofToolCommand(SpoofToolConfig config, final FlameConfigService flameConfigService) {
    this.config = config;
    this.flameConfigService = flameConfigService;
  }

  @Execute(name = "setmin")
  void setMin(@Context Player player) {
    Location location = player.getLocation();
    config.setMinLocation(location);
    flameConfigService.save(SpoofToolConfig.class);
    player.sendMessage("min location set to " + location);
  }

  @Execute(name = "setmax")
  void setMax(@Context Player player) {
    Location location = player.getLocation();
    config.setMaxLocation(location);
    flameConfigService.save(SpoofToolConfig.class);
    player.sendMessage("max location set to " + location);
  }

}
