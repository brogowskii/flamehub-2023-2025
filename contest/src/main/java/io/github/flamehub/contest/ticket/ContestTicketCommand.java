package io.github.flamehub.contest.ticket;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.contest.ContestFacade;
import org.bukkit.entity.Player;

@Command(name = "bilet", aliases = {"ticket", "contestticket"})
public final class ContestTicketCommand {

  private final ContestFacade contestFacade;
  private final ContestTicketFacade contestTicketFacade;

  public ContestTicketCommand(
      final ContestFacade contestFacade,
      final ContestTicketFacade contestTicketFacade) {
    this.contestFacade = contestFacade;
    this.contestTicketFacade = contestTicketFacade;
  }

}
