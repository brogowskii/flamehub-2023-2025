package io.github.flamehub.commons.bukkit.command;

import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.cooldown.Cooldown;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerFacade;
import java.time.temporal.ChronoUnit;
import org.bukkit.entity.Player;

@Command(name = "helpop")
public final class HelpopCommand {

  private final BukkitMessagesService messagesService;

  private final NetworkServerFacade networkServerFacade;
  private final NetworkPlayerCache networkPlayerCache;
  private final NetworkMessageService networkMessageService;

  public HelpopCommand(
      final BukkitMessagesService messagesService,
      final NetworkServerFacade networkServerFacade,
      final NetworkPlayerCache networkPlayerCache,
      final NetworkMessageService networkMessageService
  ) {
    this.messagesService = messagesService;
    this.networkServerFacade = networkServerFacade;
    this.networkPlayerCache = networkPlayerCache;
    this.networkMessageService = networkMessageService;
  }

  @Async
  @Execute
  @Cooldown(key = "helpop", count = 30, unit = ChronoUnit.SECONDS)
  public void execute(@Context final Player player, @Join final String message) {
    final NetworkPlayer networkPlayer = networkPlayerCache.findByName(player.getName());
    final String formattedMessage = messagesService.message("helpop.message.format")
        .with("player", player.getName())
        .with("server", networkServerFacade.getCurrent().getName())
        .with("proxy", networkPlayer.getProxy() == null ? "proxy=null" : networkPlayer.getProxy())
        .with("message", message)
        .applyFirst();
    player.sendMessage(TextUtil.parse(formattedMessage));

    networkMessageService.send(
        formattedMessage,
        NetworkMessageFilter.builder()
            .targetPermission("helpop.access")
            .build(),
        NetworkMessageType.CHAT
    );

  }

}
