package io.github.flamehub.economy;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.economy.user.EconomyUser;
import org.bukkit.entity.Player;

@Command(name = "balance", aliases = {"bal", "money", "pieniadze", "stankonta"})
final class BalanceCommand {

  private final BukkitMessagesService messagesService;

  BalanceCommand(final BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Execute
  void execute(@Context final Player player, @Context final EconomyUser economyUser) {

    this.messagesService.message("economy.account.balance")
        .with("formatted_money",
            NumberConverter.convertNumber(economyUser.getMoney().doubleValue()))
        .send(player);

  }

}
