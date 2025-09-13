package io.github.flamehub.commons.bukkit.config;

import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.flag.Flag;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;

@Command(name = "flameconfig")
@Permission("server.commands.flameconfig")
public final class FlameConfigCommand {

  private final FlameConfigService flameConfigService;

  public FlameConfigCommand(final FlameConfigService flameConfigService) {
    this.flameConfigService = flameConfigService;
  }

  @Async
  @Execute(name = "refresh")
  void refresh(final @Context CommandSender sender, final @Flag("-b") boolean broadcast) throws IllegalAccessException {
    int i = 0;
    for (final FlameConfig value : flameConfigService.getConfigInstancesByClassName().values()) {
      i++;
      if (broadcast) {
        flameConfigService.refreshAndBroadcast(value.getClass());
        continue;
      }

      flameConfigService.refresh(value.getClass());
    }

    BukkitMessage.from("&aPomyślnie załadowano najnowsze dane z " + i + " plików konfiguracyjnych.")
        .deliver(sender);
  }


}
