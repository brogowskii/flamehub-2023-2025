package io.github.flamehub.essentials.warp;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class WarpConfigurator {

  public WarpFacade warpFacade(
      final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
      final FlameConfigService flameConfigService,
      final TeleporterService teleporterService
  ) {
    final WarpConfig warpConfig = flameConfigService.getOrCreate(WarpConfig.class);
    final WarpFacade warpFacade = new WarpFacade(new WarpService(warpConfig));

    liteCommandsBuilder.commands(LiteCommandsAnnotations.of(
        new WarpCommand(warpFacade, teleporterService),
        new WarpCommandAdmin(warpFacade, flameConfigService))
    );

    return warpFacade;
  }

}
