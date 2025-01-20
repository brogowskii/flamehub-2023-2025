package io.github.flamehub.commons.bukkit.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import org.bukkit.entity.Player;

@Command(name = "adminchat", aliases = "ac")
@Permission("server.commands.adminchat")
public final class AdminChatCommand {

  private final BukkitMessagesService messagesService;
  private final NetworkMessageService networkMessageService;

  public AdminChatCommand(BukkitMessagesService messagesService,
      NetworkMessageService networkMessageService) {
    this.messagesService = messagesService;
    this.networkMessageService = networkMessageService;
  }

  @Execute
  void chat(@Context Player player, @Join String content) {
    String formattedMessage = messagesService.message("adminchat.message.format")
        .with("player", player.getName())
        .with("message", content)
        .applyFirst();

    networkMessageService.send(
        formattedMessage,
        NetworkMessageFilter.builder()
            .targetPermission("adminchat.access")
            .build(),
        NetworkMessageType.CHAT
    );

  }

}
