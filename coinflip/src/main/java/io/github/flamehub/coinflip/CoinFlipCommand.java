package io.github.flamehub.coinflip;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

@Command(name = "coinflip", aliases = "cf")
public final class CoinFlipCommand {

  @Execute(name = "gry")
  void games(final @Context Player player) {

  }

  @Execute(name = "stworz")
  void create(final @Context Player player) {

  }

  @Execute(name = "odbierz")
  void receive(final @Context Player player) {

  }

}
