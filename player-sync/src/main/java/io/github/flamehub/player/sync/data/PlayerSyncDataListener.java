package io.github.flamehub.player.sync.data;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.LocationUtil;
import io.github.flamehub.player.sync.PlayerSyncConfig;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerSyncDataListener implements Listener {

  private final FlameDispatcher flameDispatcher;

  private final PlayerSyncConfig playerSyncConfig;
  private final PlayerSyncDataRepository playerSyncDataRepository;

  private final Map<UUID, Long> lastConnections = new ConcurrentHashMap<>();

  public PlayerSyncDataListener(
      final FlameDispatcher flameDispatcher,
      final PlayerSyncConfig playerSyncConfig,
      final PlayerSyncDataRepository playerSyncDataRepository
  ) {
    this.flameDispatcher = flameDispatcher;
    this.playerSyncConfig = playerSyncConfig;
    this.playerSyncDataRepository = playerSyncDataRepository;
  }

  @EventHandler
  public void onPreLogin(final AsyncPlayerPreLoginEvent event) {

    final Long lastConnect = lastConnections.get(event.getUniqueId());
    if (lastConnect != null
        && lastConnect + TimeUnit.SECONDS.toMillis(3) > System.currentTimeMillis()) {
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
          TextUtil.parse("&cNie możesz się tak szybko połączyć!"));
    }

  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    player.setGameMode(GameMode.SURVIVAL);
    player.setAllowFlight(false);
    player.setFlying(false);

    CompletableFuture.supplyAsync(() -> playerSyncDataRepository.load(player.getUniqueId()))
        .thenAccept(playerSyncData -> {
          if (playerSyncData == null) {
            return;
          }

          flameDispatcher.dispatch(() -> apply(player, playerSyncData));

        }).exceptionally(throwable -> {
          throwable.printStackTrace();
          player.kick(TextUtil.parse(
              "&cWystąpił krytyczny błąd podczas wczytywania danych gracza! Zgłoś to jak najszybciej administracji!"));
          return null;
        });


  }


  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {

    final Player player = event.getPlayer();
    lastConnections.put(player.getUniqueId(), System.currentTimeMillis());

    flameDispatcher.dispatchAsync(
        () -> playerSyncDataRepository.save(PlayerSyncDataFactory.create(player)));
  }


  void apply(final Player player, final PlayerSyncData playerSyncData) {
    flameDispatcher.dispatch(() -> {
      final World world = Bukkit.getWorld("world");
      final Location spawnLocation =
          world == null ? LocationUtil.deserialize(playerSyncData.getSerializedLocation())
              : playerSyncConfig.getSpawnLocation();

      PlayerSyncDataApplicator.apply(player, playerSyncData, spawnLocation);
    });
  }


}
