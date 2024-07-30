package io.github.flamehub.achievements.achievement;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.achievements.achievement.user.AchievementUserCache;
import io.github.flamehub.achievements.achievement.user.AchievementUserRepository;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.server.NetworkServerCache;
import org.bukkit.entity.Player;


@Command(name = "os", aliases = {"osiagniecia", "achievements"})
@Permission("server.commands.achievements")
public final class AchievementCommand {

  private final AchievementConfig achievementConfig;
  private final AchievementService achievementService;
  private final AchievementUserCache achievementUserCache;
  private final AchievementUserRepository achievementUserRepository;
  private final NetworkMessageService networkMessageService;
  private final NetworkServerCache networkServerCache;

  public AchievementCommand(AchievementConfig achievementConfig,
      AchievementService achievementService, AchievementUserCache achievementUserCache,
      AchievementUserRepository achievementUserRepository,
      NetworkMessageService networkMessageService, NetworkServerCache networkServerCache) {
    this.achievementConfig = achievementConfig;
    this.achievementService = achievementService;
    this.achievementUserCache = achievementUserCache;
    this.achievementUserRepository = achievementUserRepository;
    this.networkMessageService = networkMessageService;
    this.networkServerCache = networkServerCache;
  }

  @Execute
  void execute(@Context Player player) {
    AchievementGui achievementGui = new AchievementGui(player, this.achievementConfig,
        this.achievementService, this.achievementUserCache, this.achievementUserRepository,
        networkMessageService, networkServerCache);
    achievementGui.openSelection();
  }

}
