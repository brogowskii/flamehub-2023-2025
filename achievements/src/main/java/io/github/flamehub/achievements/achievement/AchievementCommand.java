package io.github.flamehub.achievements.achievement;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.achievements.achievement.user.AchievementUserCache;
import io.github.flamehub.achievements.achievement.user.AchievementUserRepository;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.server.NetworkServerFacade;
import org.bukkit.entity.Player;


@Command(name = "os", aliases = {"osiagniecia", "achievements"})
@Permission("server.commands.achievements")
public final class AchievementCommand {

  private final AchievementConfig achievementConfig;
  private final AchievementService achievementService;
  private final AchievementUserCache achievementUserCache;
  private final AchievementUserRepository achievementUserRepository;
  private final NetworkMessageService networkMessageService;
  private final NetworkServerFacade networkServerFacade;

  public AchievementCommand(
      final AchievementConfig achievementConfig,
      final AchievementService achievementService,
      final AchievementUserCache achievementUserCache,
      final AchievementUserRepository achievementUserRepository,
      final NetworkMessageService networkMessageService,
      final NetworkServerFacade networkServerFacade) {
    this.achievementConfig = achievementConfig;
    this.achievementService = achievementService;
    this.achievementUserCache = achievementUserCache;
    this.achievementUserRepository = achievementUserRepository;
    this.networkMessageService = networkMessageService;
    this.networkServerFacade = networkServerFacade;
  }

  @Execute
  void execute(@Context final Player player) {
    final AchievementGui achievementGui = new AchievementGui(player, achievementConfig,
        achievementService, achievementUserCache, achievementUserRepository,
        networkMessageService, networkServerFacade);
    achievementGui.openSelection();
  }

}
