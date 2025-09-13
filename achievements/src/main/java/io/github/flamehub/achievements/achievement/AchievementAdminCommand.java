package io.github.flamehub.achievements.achievement;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresher;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;

@Command(name = "achievementadmin")
@Permission("server.commands.achievementadmin")
public final class AchievementAdminCommand extends FlameConfigRefresher {

  public AchievementAdminCommand(final FlameConfigService flameConfigService) {
    super(flameConfigService, AchievementConfig.class);
  }

  @Execute(name = "reload")
  void reload(final @Context CommandSender sender) {
    refresh(sender);
  }

  @Execute(name = "update")
  void update(final @Context CommandSender sender) {
    refreshAndBroadcast(sender);
  }

}
