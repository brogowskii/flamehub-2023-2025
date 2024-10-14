package io.github.flamehub.commons.bukkit.config;

import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;

public class FlameConfigRefresherCommand extends FlameConfigRefresher{

  public FlameConfigRefresherCommand(
      final FlameConfigService flameConfigService,
      final Class<? extends FlameConfig> configClass) {
    super(flameConfigService, configClass);
  }

  @Execute(name = "reload")
  void reload(@Context CommandSender sender) {
    super.refreshConfigLocally(sender);
  }

  @Execute(name = "update")
  void update(@Context CommandSender sender) {
    super.refreshConfigRemote(sender);
  }

}
