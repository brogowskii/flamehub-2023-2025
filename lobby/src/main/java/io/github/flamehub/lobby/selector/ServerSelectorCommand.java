package io.github.flamehub.lobby.selector;

import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.MongoConfigService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@Command(name = "serverselector")
@Permission("server.commands.serverselector")
public final class ServerSelectorCommand {

    private final Plugin plugin;
    private final ServerSelectorConfig serverSelectorConfig;
    private final MongoConfigService mongoConfigService;

    public ServerSelectorCommand(Plugin plugin, ServerSelectorConfig serverSelectorConfig, MongoConfigService mongoConfigService) {
        this.plugin = plugin;
        this.serverSelectorConfig = serverSelectorConfig;
        this.mongoConfigService = mongoConfigService;
    }

    @Async
    @Execute(name = "reload")
    void execute(@Context CommandSender sender) {
        try {
            this.mongoConfigService.refresh(ServerSelectorConfig.class, this.serverSelectorConfig);
            BukkitMessage.from("&aPomyślnie przeładowano plik konfiguracyjny pluginu &2server-selector&a!").send(sender);

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Wystąpił błąd podczas odświeżania configu: " + this.serverSelectorConfig.getId());
        }

    }


}
