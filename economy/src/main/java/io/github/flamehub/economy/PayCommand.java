package io.github.flamehub.economy;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.cooldown.Cooldown;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.network.message.NetworkMessageFilterBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.economy.user.EconomyUser;
import io.github.flamehub.economy.user.EconomyUserFacade;
import java.time.temporal.ChronoUnit;
import org.bukkit.entity.Player;

@Command(name = "pay", aliases = {"przelej", "przelew", "przelejpieniadze"})
final class PayCommand {

  private final EconomyUserFacade economyUserFacade;
  private final BukkitMessagesService messagesService;
  private final NetworkMessageService networkMessageService;

  PayCommand(
      final EconomyUserFacade economyUserFacade,
      final BukkitMessagesService messagesService,
      final NetworkMessageService networkMessageService
  ) {
    this.economyUserFacade = economyUserFacade;
    this.messagesService = messagesService;
    this.networkMessageService = networkMessageService;
  }

  @Execute
  @Cooldown(key = "pay", count = 10, unit = ChronoUnit.SECONDS)
  void execute(@Context Player player, @Context EconomyUser economyUser, @Arg Player target,
      @Arg double value) {

    if (player.getUniqueId().equals(target.getUniqueId())) {
      return;
    }

    final EconomyUser targetEconomyUser = economyUserFacade.findByUniqueId(
        target.getUniqueId());
    if (targetEconomyUser == null) {
      messagesService.sendMessage(player, "user.does.not.exist");
      return;
    }

    if (economyUser.getMoney().doubleValue() < value || value <= 0) {
      messagesService.sendMessage(player, "economy.not.enough.money");
      return;
    }

    economyUser.removeMoney(value);
    economyUser.setNeedUpdate(true);
    targetEconomyUser.addMoney(value);
    targetEconomyUser.setNeedUpdate(true);

    networkMessageService.send(
        "&8[&6&lPRZELEWY&8] &7Gracz &f" + player.getName() + " &7przelał graczowi &f"
            + target.getName() + " &7kwote o wysokości: &e$" + NumberConverter.convertNumber(value),
        new NetworkMessageFilterBuilder()
            .targetServerCategory(
                CommonsPlugin.getInstance().getNetworkServerCache().getCurrent().getCategory())
            .targetPermission("server.eco.logs")
            .build(),
        NetworkMessageType.CHAT
    );

    messagesService.getAsText("economy.pay")
        .placeholder("{PLAYER}", target.getName())
        .placeholder("{VALUE}", RoundUtil.round(value, 2))
        .send(player);

    messagesService.getAsText("economy.pay.received")
        .placeholder("{PLAYER}", player.getName())
        .placeholder("{VALUE}", RoundUtil.round(value, 2))
        .send(target);

  }

}
