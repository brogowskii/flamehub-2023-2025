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

  public InvalidUsageHandlerImpl(final BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Override
  public void handle(final Invocation<CommandSender> invocation, final InvalidUsage<CommandSender> result,
      final ResultHandlerChain<CommandSender> chain) {
    final CommandSender sender = invocation.sender();
    final List<String> schematics = result.getSchematic().all();

    final String message = messagesService.getMessage("cmd.invalid.usage");
    final String usage = schematics.getFirst();
    if (schematics.size() == 1) {
      BukkitMessage.from(message).with("correct_usage", usage).deliver(sender);
      return;
    }

    BukkitMessage.from(message).with("correct_usage", "").deliver(sender);
    for (final String sch : schematics) {
      messagesService.message("cmd.invalid.usage.multiple")
          .with("correct_usage", sch)
          .deliver(sender);
    }
  }
}