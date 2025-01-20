package io.github.flamehub.commons.bukkit.automessage;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;

@Command(name = "automessage")
@Permission("server.commands.automessage")
public final class AutoMessageReloadCommand {

  private final FlameConfigService flameConfigService;

  public AutoMessageReloadCommand(FlameConfigService flameConfigService) {
    this.flameConfigService = flameConfigService;
  }

  @Execute
  void execute(@Context CommandSender sender) {
    try {
      flameConfigService.refreshLocally(AutoMessageConfig.class);
      BukkitMessage.from("&aAutoMessage config reloaded!").deliver(sender);
    } catch (IllegalAccessException e) {
      BukkitMessage.from("&cError while reloading AutoMessage config!").deliver(sender);
      throw new RuntimeException(e);
    }
  }

}
