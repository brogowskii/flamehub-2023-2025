package io.github.flamehub.report;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import org.bukkit.entity.Player;

@Command(name = "reportadmin", aliases = "zglosadmin")
@Permission("server.commands.reportadmin")
public final class ReportAdminCommand {

  private final FlameDispatcher flameDispatcher;
  private final ReportRepository reportRepository;

  public ReportAdminCommand(
      final FlameDispatcher flameDispatcher,
      final ReportRepository reportRepository
  ) {
    this.flameDispatcher = flameDispatcher;
    this.reportRepository = reportRepository;
  }

  @Execute
  void exec(@Context final Player player) {
    final ReportAdminGui reportAdminGui = new ReportAdminGui(flameDispatcher, reportRepository);
    reportAdminGui.open(player);
  }

}
