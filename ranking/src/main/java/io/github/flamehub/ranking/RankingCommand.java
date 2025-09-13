package io.github.flamehub.ranking;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.ranking.gui.RankingGui;
import io.github.flamehub.ranking.gui.RankingGuiCache;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "ranking")
public final class RankingCommand {

  private final RankingGuiCache rankingGuiCache;
  private final RankingPlugin rankingPlugin;

  public RankingCommand(final RankingGuiCache rankingGuiCache, final RankingPlugin rankingPlugin) {
    this.rankingGuiCache = rankingGuiCache;
    this.rankingPlugin = rankingPlugin;
  }

  @Execute
  public void execute(@Context final Player player, @Arg final String type) {

    final RankingGui gui = rankingGuiCache.findById(type);
    if (gui == null) {
      player.sendMessage("null");
      return;
    }

    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    gui.open(player);
  }

  @Execute(name = "reload")
  @Permission("server.commands.ranking.reload")
  public void reload(@Context final CommandSender sender) {
    rankingPlugin.loadRankings();
    BukkitMessage.from("&aPomyślnie przeładowano rankingi!")
        .deliver(sender);
  }

}
