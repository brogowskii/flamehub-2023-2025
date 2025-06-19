package io.github.flamehub.player.sync.data;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.LocationUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.server.NetworkServer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

  public final static ExecutorService VIRTUAL_THREAD_PER_TASK = Executors.newVirtualThreadPerTaskExecutor();

  private final NetworkServer current;
  private final RedisMessenger redisMessenger;
  private final FlameDispatcher flameDispatcher;
  private final PlayerSyncDataRepository playerSyncDataRepository;


  private final Map<UUID, Long> lastConnections = new ConcurrentHashMap<>();

  public PlayerSyncDataListener(
      final RedisMessenger redisMessenger,
      final FlameDispatcher flameDispatcher,
      final NetworkServer current, final PlayerSyncDataRepository playerSyncDataRepository) {
    this.redisMessenger = redisMessenger;
    this.flameDispatcher = flameDispatcher;
    this.current = current;
    this.playerSyncDataRepository = playerSyncDataRepository;
  }

  @EventHandler
  public void onPreLogin(AsyncPlayerPreLoginEvent event) {

    Long lastConnect = lastConnections.get(event.getUniqueId());
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
          player.kick(TextUtil.parse("&cWystkrytyczny bpodczas danych, zgto administracji!"));
          return null;
        });


  }


  @EventHandler
  public void onQuit(PlayerQuitEvent event) {

    final Player player = event.getPlayer();
    lastConnections.put(player.getUniqueId(), System.currentTimeMillis());

    flameDispatcher.dispatchAsync(
        () -> playerSyncDataRepository.save(PlayerSyncDataFactory.create(player)));
  }


  void apply(Player player, PlayerSyncData playerSyncData) {
    flameDispatcher.dispatch(() -> {
      World world = Bukkit.getWorld("world");
      Location spawnLocation;
      if (world == null) {
        spawnLocation = LocationUtil.deserialize(playerSyncData.getSerializedLocation());
      } else {
        Location worldSpawnLocation = world.getSpawnLocation().clone();
        worldSpawnLocation.setPitch(0);
        worldSpawnLocation.setYaw(0);
        spawnLocation = worldSpawnLocation.toCenterLocation();
      }

      PlayerSyncDataApplicator.apply(player, playerSyncData, spawnLocation);
    });
  }


}
