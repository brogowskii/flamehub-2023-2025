package io.github.flamehub.contest;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.contest.ticket.ContestTicketFacade;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

@Command(name = "konkursa", aliases = {"contestadmin", "ca"})
final class ContestAdminCommand {

  private final ContestFacade contestFacade;
  private final ContestTicketFacade contestTicketFacade;

  ContestAdminCommand(
      final ContestFacade contestFacade,
      final ContestTicketFacade contestTicketFacade
  ) {
    this.contestFacade = contestFacade;
    this.contestTicketFacade = contestTicketFacade;
  }

  @Execute(name = "points")
  @Permission("contest.admin.points")
  void showPoints(@Context final CommandSender sender, @Arg final NetworkPlayer target) {
    final double points = contestFacade.balance(target.getUniqueId());
    BukkitMessage.from("&7Gracz &a" + target.getName() + " &7posiada &a" + points + " &7punktów konkursu.")
        .deliver(sender);
  }

  @Execute(name = "addpoints")
  @Permission("contest.admin.addpoints")
  void addPoints(@Context final CommandSender sender, @Arg final NetworkPlayer target, @Arg final int amount) {
    if (amount <= 0) {
      BukkitMessage.from("&cIlość punktów musi być większa od 0!").deliver(sender);
      return;
    }

    contestFacade.addPoints(target.getUniqueId(), amount);
    if (!(sender instanceof ConsoleCommandSender)) {
      BukkitMessage.from(
              "&7Dodano &a" + amount + " &7punktów graczowi &a" + target.getName() + "&7.")
          .deliver(sender);
    }
  }

  @Execute(name = "removepoints")
  @Permission("contest.admin.removepoints")
  void removePoints(@Context final CommandSender sender, @Arg final NetworkPlayer target, @Arg final int amount) {
    if (amount <= 0) {
      BukkitMessage.from("&cIlość punktów musi być większa od 0!").deliver(sender);
      return;
    }

    final double currentPoints = contestFacade.balance(target.getUniqueId());
    if (currentPoints < amount) {
      BukkitMessage.from("&cGracz nie posiada wystarczająco punktów! Obecny stan: &7" + currentPoints)
          .deliver(sender);
      return;
    }

    contestFacade.removePoints(target.getUniqueId(), amount);
    BukkitMessage.from("&7Odebrano &c" + amount + " &7punktów graczowi &a" + target.getName() + "&7.")
        .deliver(sender);
  }

  @Execute(name = "setpoints")
  @Permission("contest.admin.setpoints")
  void setPoints(@Context final CommandSender sender, @Arg final NetworkPlayer target, @Arg final int amount) {
    if (amount < 0) {
      BukkitMessage.from("&cIlość punktów nie może być ujemna!").deliver(sender);
      return;
    }

    contestFacade.setPoints(target.getUniqueId(), amount);
    BukkitMessage.from("&7Ustawiono &a" + amount + " &7punktów graczowi &a" + target.getName() + "&7.")
        .deliver(sender);
  }

  @Execute(name = "tickets")
  @Permission("contest.admin.tickets")
  void showTickets(@Context final CommandSender sender, @Arg final NetworkPlayer target) {
    contestTicketFacade.getTicketsAmount(target.getUniqueId())
        .thenAccept(amount -> {
          BukkitMessage.from("&7Gracz &a" + target.getName() + " &7posiada &a" + amount + " &7biletów.")
              .deliver(sender);
        });
  }

  @Execute(name = "createticket")
  @Permission("contest.admin.createticket")
  void createTicket(@Context final CommandSender sender, @Arg final NetworkPlayer target) {
    contestTicketFacade.createTicket(target.getUniqueId(), target.getName())
        .thenRun(() -> {
          BukkitMessage.from("&7Utworzono bilet dla gracza &a" + target.getName() + "&7.")
              .deliver(sender);
        });
  }

}
