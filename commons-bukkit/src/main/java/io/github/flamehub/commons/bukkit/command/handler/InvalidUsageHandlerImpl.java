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

    String message = this.messagesService.getMessage("cmd.invalid.usage");
    String usage = schematics.get(0);
    if (schematics.size() == 1) {
      BukkitMessage.from(message).with("correct_usage", usage).send(sender);
      return;
    }

    BukkitMessage.from(message).with("correct_usage", "").send(sender);
    for (String sch : schematics) {
      this.messagesService.message("cmd.invalid.usage.multiple")
          .with("correct_usage", sch)
          .send(sender);
    }
  }
}