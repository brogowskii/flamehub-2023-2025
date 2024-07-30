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

  public CensureCommand(FlameConfigService flameConfigService) {
    this.flameConfigService = flameConfigService;
  }

  @Execute
  void reload(@Context CommandSender sender) {
    try {
      this.flameConfigService.update(CensureConfig.class);
      BukkitMessage.from("&aPomyślnie przeładowano konfigurację cenzury!").send(sender);
    } catch (IllegalAccessException e) {
      BukkitMessage.from("&cWystąpił błąd podczas przeładowywania konfiguracji cenzury!")
          .send(sender);
      throw new RuntimeException(e);
    }

  }

}
