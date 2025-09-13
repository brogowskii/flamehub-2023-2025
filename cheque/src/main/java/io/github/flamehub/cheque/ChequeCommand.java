package io.github.flamehub.cheque;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import java.util.concurrent.CompletableFuture;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "cheque", aliases = "czek")
public final class ChequeCommand {

  private final ChequeService chequeService;
  private final ChequeLogRepository chequeLogRepository;
  private final Economy economy;

  public ChequeCommand(
      final ChequeService chequeService,
      final ChequeLogRepository chequeLogRepository,
      final Economy economy
  ) {
    this.chequeService = chequeService;
    this.chequeLogRepository = chequeLogRepository;
    this.economy = economy;
  }

  @Execute
  void execute(final @Context Player player, final @Arg double money) {

    if (Double.isNaN(money) || Double.isInfinite(money)) {
      return;
    }

    if (money < 1) {
      BukkitMessage.from("&cMinimalnie możesz wytworzyć czek na 1$.").deliver(player);
      return;
    }

    if (economy.getBalance(player) < money) {
      BukkitMessage.from("&cNie masz wystarczająco pieniędzy.").deliver(player);
      return;
    }

    economy.withdrawPlayer(player, money);
    final ItemStack itemStack = chequeService.generateCheque(money, player);
    player.getInventory().addItem(itemStack.clone());

    CompletableFuture.runAsync(() -> {
      final ChequeLog chequeLog = ChequeLog.builder()
          .type(ChequeLogType.CREATE)
          .who(player.getName())
          .money(money)
          .build();
      chequeLogRepository.save(chequeLog);
      chequeService.sendWebhook(chequeLog);
    });

  }
}
