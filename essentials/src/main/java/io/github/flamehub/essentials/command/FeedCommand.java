package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import org.bukkit.entity.Player;

@Command(name = "feed")
@Permission("server.essentials.commands.feed")
final class FeedCommand {

  private final BukkitMessagesService messagesService;

  public FeedCommand(final BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Execute
  void execute(@Context final Player player) {
    player.setFoodLevel(20);
    player.setSaturation(20);
    player.setExhaustion(0);
    this.messagesService.sendMessage(player, "feed.success");
  }

}
