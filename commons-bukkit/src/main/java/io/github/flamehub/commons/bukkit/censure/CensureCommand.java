package io.github.flamehub.commons.bukkit.censure;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;

@Command(name = "censure")
@Permission("server.commands.censure")
public final class CensureCommand {

  private final FlameConfigService flameConfigService;

  public CensureCommand(final FlameConfigService flameConfigService) {
    this.flameConfigService = flameConfigService;
  }

  @Execute
  void reload(@Context final CommandSender sender) {
    try {
      flameConfigService.refreshAndBroadcast(CensureConfig.class);
      BukkitMessage.from("&aPomyślnie przeładowano konfigurację cenzury!").deliver(sender);
    } catch (final IllegalAccessException e) {
      BukkitMessage.from("&cWystąpił błąd podczas przeładowywania konfiguracji cenzury!")
          .deliver(sender);
      throw new RuntimeException(e);
    }

  }

}
