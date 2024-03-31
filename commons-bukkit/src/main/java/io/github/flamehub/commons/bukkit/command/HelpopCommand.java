package io.github.flamehub.commons.bukkit.command;

import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import org.bukkit.entity.Player;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.util.TimeUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Command(name = "helpop", aliases = {"zgloszenie", "report"})
public final class HelpopCommand {

    private final BukkitMessagesService messagesService;

    private final NetworkServerCache networkServerCache;
    private final NetworkPlayerCache networkPlayerCache;
    private final NetworkMessageService networkMessageService;

    public HelpopCommand(
            BukkitMessagesService messagesService,
            NetworkServerCache networkServerCache,
            NetworkPlayerCache networkPlayerCache,
            NetworkMessageService networkMessageService
    ) {
        this.messagesService = messagesService;
        this.networkServerCache = networkServerCache;
        this.networkPlayerCache = networkPlayerCache;
        this.networkMessageService = networkMessageService;
    }

    @Async
    @Execute
    public void execute(@Context Player player, @Join String message) {
        NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(player.getName());
        Instant helpopDelay = networkPlayer.getHelpopDelay();

        if (helpopDelay.isAfter(Instant.now())) {
            this.messagesService.message("helpop.message.cooldown")
                    .with("time", TimeUtil.formatTimeSimple(Duration.between(Instant.now(), helpopDelay)))
                    .send(player);
            return;
        }

        networkPlayer.setHelpopDelay(Instant.now().plus(15, ChronoUnit.SECONDS));
        this.networkPlayerCache.save(networkPlayer);

        String formattedMessage = this.messagesService.message("helpop.message.format")
                .with("player", player.getName())
                .with("server", this.networkServerCache.getCurrent().getName())
                .with("proxy", networkPlayer.getProxy() == null ? "proxy=null" : networkPlayer.getProxy())
                .with("message", message)
                .applyFirst();
        player.sendMessage(TextUtil.parse(formattedMessage));

        this.networkMessageService.send(
                formattedMessage,
                NetworkMessageFilter.builder()
                        .targetPermission("helpop.access")
                        .build(),
                NetworkMessageType.CHAT
        );

    }

}
