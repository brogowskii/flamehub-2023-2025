package io.github.flamehub.contest;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.contest.ticket.ContestTicketFacade;
import org.bukkit.entity.Player;

@Command(name = "konkurs", aliases = "event")
final class ContestCommand {

  private final FlameDispatcher flameDispatcher;
  private final ContestFacade contestFacade;
  private final ContestTicketFacade contestTicketFacade;

  ContestCommand(
      final FlameDispatcher flameDispatcher,
      final ContestFacade contestFacade,
      final ContestTicketFacade contestTicketFacade
  ) {
    this.flameDispatcher = flameDispatcher;
    this.contestFacade = contestFacade;
    this.contestTicketFacade = contestTicketFacade;
  }

  @Execute
  void open(final @Context Player player) {
    final ContestGui contestGui = new ContestGui(
        flameDispatcher,
        contestFacade,
        contestTicketFacade
    );
    contestGui.openGui(player);
  }
}
