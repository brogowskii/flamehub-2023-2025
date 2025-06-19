package io.github.flamehub.missions;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.missions.user.MissionUser;
import io.github.flamehub.missions.user.MissionUserCache;
import io.github.flamehub.missions.user.MissionUserRepository;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "misje", aliases = "missions")
public final class MissionCommand {

  private final FlameDispatcher flameDispatcher;
  private final FlameConfigService flameConfigService;
  private final MissionConfig missionConfig;
  private final MissionUserCache missionUserCache;
  private final MissionUserRepository missionUserRepository;

  public MissionCommand(
      final FlameDispatcher flameDispatcher, final FlameConfigService flameConfigService,
      final MissionConfig missionConfig,
      final MissionUserCache missionUserCache, final MissionUserRepository missionUserRepository) {
    this.flameDispatcher = flameDispatcher;
    this.flameConfigService = flameConfigService;
    this.missionConfig = missionConfig;
    this.missionUserCache = missionUserCache;
    this.missionUserRepository = missionUserRepository;
  }

  @Execute
  void exec(@Context Player player) {
    new MissionGui(missionUserCache, missionConfig).open(player);
  }

  @Execute(name = "reload")
  @Permission("server.commands.missions.reload")
  void reload(@Context Player player) throws IllegalAccessException {
    flameConfigService.refreshLocally(MissionConfig.class);
  }

  @Execute(name = "reset")
  @Permission("server.commands.missions.reset")
  void reload(@Context CommandSender sender, @Arg String target) throws IllegalAccessException {
    final MissionUser byName = missionUserCache.findByName(target);
    if (byName == null) {
      return;
    }

    byName.getActiveMissions().clear();
    missionUserRepository.save(byName);
    sender.sendMessage("Zresetowano misje gracza " + byName.getName() + ".");

  }

}
