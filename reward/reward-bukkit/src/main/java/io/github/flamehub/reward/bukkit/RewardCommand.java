package io.github.flamehub.reward.bukkit;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.reward.api.RewardReceivedEntryRepository;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Command(name = "reward", aliases = {"nagroda", "nagrody"})
public final class RewardCommand {

  private final RewardReceivedEntryRepository rewardReceivedEntryRepository;
  private final NetworkServerCache networkServerCache;

  public RewardCommand(RewardReceivedEntryRepository rewardReceivedEntryRepository,
      NetworkServerCache networkServerCache) {
    this.rewardReceivedEntryRepository = rewardReceivedEntryRepository;
    this.networkServerCache = networkServerCache;
  }

  @Execute
  void execute(@Context Player player) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    new RewardGui(player, rewardReceivedEntryRepository, networkServerCache).open();
  }

}
