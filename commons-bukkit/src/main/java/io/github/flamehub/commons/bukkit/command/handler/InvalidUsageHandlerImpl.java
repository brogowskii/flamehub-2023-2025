package io.github.flamehub.commons.bukkit.command.handler;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import java.util.List;
import org.bukkit.command.CommandSender;

public final class InvalidUsageHandlerImpl implements InvalidUsageHandler<CommandSender> {

  private final BukkitMessagesService messagesService;

  public InvalidUsageHandlerImpl(BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Override
  public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result,
      ResultHandlerChain<CommandSender> chain) {
    CommandSender sender = invocation.sender();
    List<String> schematics = result.getSchematic().all();

    String message = messagesService.getMessage("cmd.invalid.usage");
    String usage = schematics.getFirst();
    if (schematics.size() == 1) {
      BukkitMessage.from(message).with("correct_usage", usage).deliver(sender);
      return;
    }

    BukkitMessage.from(message).with("correct_usage", "").deliver(sender);
    for (String sch : schematics) {
      messagesService.message("cmd.invalid.usage.multiple")
          .with("correct_usage", sch)
          .deliver(sender);
    }
  }
}