package io.github.flamehub.daily.reward;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresher;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.daily.reward.user.DailyRewardUserCache;
import io.github.flamehub.daily.reward.user.DailyRewardUserRepository;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "daily", aliases = {"codziennanagroda", "dailyreward", "dzienna"})
@Permission("server.commands.daily")
public final class DailyRewardCommand extends FlameConfigRefresher {

  private final FlameDispatcher flameDispatcher;
  private final DailyRewardConfig dailyRewardConfig;
  private final DailyRewardUserCache dailyRewardUserCache;
  private final DailyRewardUserRepository dailyRewardUserRepository;

  public DailyRewardCommand(FlameConfigService flameConfigService, FlameDispatcher flameDispatcher,
      DailyRewardConfig dailyRewardConfig, DailyRewardUserCache dailyRewardUserCache,
      DailyRewardUserRepository dailyRewardUserRepository) {
    super(flameConfigService, DailyRewardConfig.class);
    this.flameDispatcher = flameDispatcher;
    this.dailyRewardConfig = dailyRewardConfig;
    this.dailyRewardUserCache = dailyRewardUserCache;
    this.dailyRewardUserRepository = dailyRewardUserRepository;
  }

  @Execute
  void exec(@Context Player player) {
    new DailyRewardGui(flameDispatcher, dailyRewardConfig, dailyRewardUserCache,
        dailyRewardUserRepository).open(player);
  }

  @Execute(name = "reload")
  @Permission("server.commands.daily.reload")
  void reload(@Context CommandSender sender) {
    super.refreshConfigLocally(sender);
  }

  @Execute(name = "update")
  @Permission("server.commands.daily.update")
  void update(@Context CommandSender sender) {
    super.refreshConfigRemote(sender);
  }

}
