package io.github.flamehub.essentials.spawn;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.config.MongoConfigService;
import org.bukkit.command.CommandSender;

public final class SpawnConfigurator {

    public SpawnFacade spawnFacade(
            final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
            final MongoConfigService mongoConfigService,
            final TeleporterService teleporterService
    ) {

        final SpawnConfig spawnConfig = mongoConfigService.findOrCreate(SpawnConfig.class, "spawn", SpawnConfig::new);
        final SpawnFacade spawnFacade = new SpawnFacade(spawnConfig);

        liteCommandsBuilder.commands(LiteCommandsAnnotations.of(
                new SpawnCommand(spawnFacade, teleporterService),
                new SpawnSetCommand(mongoConfigService, spawnFacade)
        ));

        return spawnFacade;
    }

}
