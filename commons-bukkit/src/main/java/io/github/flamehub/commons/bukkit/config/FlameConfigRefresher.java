package io.github.flamehub.commons.bukkit.config;

import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigService;
import java.util.concurrent.CompletableFuture;
import org.bukkit.command.CommandSender;

public class FlameConfigRefresher {

  private final FlameConfigService flameConfigService;
  private final Class<? extends FlameConfig> configClass;

  public FlameConfigRefresher(
      final FlameConfigService flameConfigService,
      final Class<? extends FlameConfig> configClass
  ) {
    this.flameConfigService = flameConfigService;
    this.configClass = configClass;
  }

  public CompletableFuture<Void> refresh(final CommandSender executor) {
    return CompletableFuture.runAsync(() -> {
      try {
        flameConfigService.refresh(configClass);
        BukkitMessage.from("&aPomyślnie załadowano najnowsze dane z pliku konfiguracyjnego.")
            .deliver(executor);
      } catch (final IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public CompletableFuture<Void> refreshAndBroadcast(final CommandSender executor) {
    return CompletableFuture.runAsync(() -> {
      try {
        flameConfigService.refreshAndBroadcast(configClass);
        BukkitMessage.from(
                "&aPomyślnie załadowano najnowsze dane z pliku konfiguracyjnego -> zaktualizowano na każdym podserwerze.")
            .deliver(executor);
      } catch (final IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });
  }


}
