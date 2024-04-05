package io.github.flamehub.essentials.warp;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.teleport.TeleporterService;
import io.github.flamehub.commons.config.MongoConfigService;
import org.bukkit.command.CommandSender;

public final class WarpConfigurator {

    public WarpFacade warpFacade(
            final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
            final MongoConfigService mongoConfigService,
            final TeleporterService teleporterService
    ) {
        final WarpConfig warpConfig = mongoConfigService.findOrCreate(WarpConfig.class, "warps", WarpConfig::new);
        final WarpFacade warpFacade = new WarpFacade(warpConfig);

        liteCommandsBuilder.commands(LiteCommandsAnnotations.of(
                new WarpCommand(warpFacade, teleporterService),
                new WarpCommandAdmin(warpFacade, mongoConfigService))
        );

        return warpFacade;
    }

}
