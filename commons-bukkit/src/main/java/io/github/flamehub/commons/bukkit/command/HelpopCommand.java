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
import io.github.flamehub.commons.server.NetworkServerCache;
import java.time.temporal.ChronoUnit;
import org.bukkit.entity.Player;

@Command(name = "helpop")
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
  @Cooldown(key = "helpop", count = 30, unit = ChronoUnit.SECONDS)
  public void execute(@Context Player player, @Join String message) {
    NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(player.getName());
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
