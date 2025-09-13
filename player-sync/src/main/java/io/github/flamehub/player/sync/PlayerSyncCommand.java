package io.github.flamehub.player.sync;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.entity.Player;

@Command(name = "playersync")
@Permission("server.commands.playersync")
public final class PlayerSyncCommand {


  private final FlameConfigService flameConfigService;
  private final PlayerSyncConfig playerSyncConfig;

  public PlayerSyncCommand(
      final FlameConfigService flameConfigService,
      final PlayerSyncConfig playerSyncConfig
  ) {
    this.flameConfigService = flameConfigService;
    this.playerSyncConfig = playerSyncConfig;
  }

  @Execute(name = "setspawn")
  void setspawn(final @Context Player player) {
    playerSyncConfig.setSpawnLocation(player.getLocation().clone());
    flameConfigService.save(PlayerSyncConfig.class);

    player.sendMessage("ustawiono");
  }
}
