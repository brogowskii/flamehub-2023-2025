package io.github.flamehub.report;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import org.bukkit.entity.Player;

@Command(name = "zglos", aliases = {"zgloszenie", "report"})
public final class ReportCommand {

  private final ReportRepository reportRepository;
  private final NetworkServerFacade networkServerFacade;
  private final NetworkMessageService networkMessageService;
  private final FlameDispatcher flameDispatcher;

  public ReportCommand(
      final ReportRepository reportRepository,
      final NetworkServerFacade networkServerFacade,
      final NetworkMessageService networkMessageService,
      final FlameDispatcher flameDispatcher
  ) {
    this.reportRepository = reportRepository;
    this.networkServerFacade = networkServerFacade;
    this.networkMessageService = networkMessageService;
    this.flameDispatcher = flameDispatcher;
  }

  @Execute
  void exec(@Context final Player player, @Arg final NetworkPlayer target) {

    if (player.getName().equals(target.getName())) {
      BukkitMessage.from("&cNie możesz zgłosić samego siebie!").deliver(player);
      return;
    }

    if (reportRepository.findByReporterAndTarget(player.getName(), target.getName()) != null) {
      BukkitMessage.from(
              "&cZgłosiłeś już tego gracza! Poczekaj cierpliwie aż administracja sprawdzi zgłoszenie.")
          .deliver(player);
      return;
    }

    final ReportGui reportGui = new ReportGui(
        reportRepository,
        networkServerFacade,
        networkMessageService,
        flameDispatcher
    );
    reportGui.open(player, target);

  }

}
