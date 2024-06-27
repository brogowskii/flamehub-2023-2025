package io.github.flamehub.commons.bukkit.censure;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.legacy.config.MongoConfigService;
import org.bukkit.command.CommandSender;

@Command(name = "censure")
@Permission("server.commands.censure")
public final class CensureCommand {

    private final MongoConfigService mongoConfigService;
    private final CensureConfig config;

    public CensureCommand(MongoConfigService mongoConfigService, CensureConfig config) {
        this.mongoConfigService = mongoConfigService;
        this.config = config;
    }

    @Execute
    void reload(@Context CommandSender sender) {
        try {
            this.mongoConfigService.refresh(CensureConfig.class, this.config);
            sender.sendMessage("Przeładowano");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
