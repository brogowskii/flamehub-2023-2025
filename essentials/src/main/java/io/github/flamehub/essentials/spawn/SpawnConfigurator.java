package io.github.flamehub.essentials.spawn;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class SpawnConfigurator {

  public SpawnFacade spawnFacade(
      final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
      final FlameConfigService flameConfigService,
      final TeleporterService teleporterService
  ) {

    final SpawnConfig spawnConfig = flameConfigService.getOrCreate(SpawnConfig.class);
    final SpawnFacade spawnFacade = new SpawnFacade(spawnConfig);

    liteCommandsBuilder.commands(LiteCommandsAnnotations.of(
        new SpawnCommand(spawnFacade, teleporterService),
        new SpawnSetCommand(flameConfigService, spawnFacade)
    ));

    return spawnFacade;
  }

}
