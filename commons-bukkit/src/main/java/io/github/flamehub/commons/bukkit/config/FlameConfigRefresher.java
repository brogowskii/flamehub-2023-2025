package io.github.flamehub.commons.bukkit.config;

import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigService;
import java.util.concurrent.CompletableFuture;
import org.bukkit.command.CommandSender;

public class FlameConfigRefresher {

  private final FlameConfigService flameConfigService;
  private final Class<? extends FlameConfig> configClass;

  public FlameConfigRefresher(FlameConfigService flameConfigService,
      Class<? extends FlameConfig> configClass) {
    this.flameConfigService = flameConfigService;
    this.configClass = configClass;
  }

  public CompletableFuture<Void> refreshConfigLocally(CommandSender executor) {
    return CompletableFuture.runAsync(() -> {
      try {
        this.flameConfigService.refreshLocally(this.configClass);
        BukkitMessage.from("&aPomyślnie załadowano najnowsze dane z pliku konfiguracyjnego.")
            .send(executor);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public CompletableFuture<Void> refreshConfigRemote(CommandSender executor) {
    return CompletableFuture.runAsync(() -> {
      try {
        this.flameConfigService.update(this.configClass);
        BukkitMessage.from(
                "&aPomyślnie załadowano najnowsze dane z pliku konfiguracyjnego -> zapisano do bazy danych -> zaktualizowano na każdym podserwerze.")
            .send(executor);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });
  }


}
