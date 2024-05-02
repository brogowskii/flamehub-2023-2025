package io.github.flamehub.player.sync.data;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.LocationUtil;
import io.github.flamehub.player.sync.PlayerSyncConfig;
import io.github.flamehub.player.sync.PlayerSyncPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class PlayerSyncDataListener implements Listener {

    private final PlayerSyncPlugin plugin;
    private final PlayerSyncConfig playerSyncConfig;
    private final FlameDispatcher flameDispatcher;
    private final PlayerSyncDataRepository playerSyncDataRepository;

    private final Map<UUID, Long> lastConnections = new ConcurrentHashMap<>();

    public PlayerSyncDataListener(PlayerSyncPlugin plugin, PlayerSyncConfig playerSyncConfig, FlameDispatcher flameDispatcher, PlayerSyncDataRepository playerSyncDataRepository) {
        this.plugin = plugin;
        this.playerSyncConfig = playerSyncConfig;
        this.flameDispatcher = flameDispatcher;
        this.playerSyncDataRepository = playerSyncDataRepository;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {

        Long lastConnect = this.lastConnections.get(event.getUniqueId());
        if (lastConnect != null && lastConnect + TimeUnit.SECONDS.toMillis(3) > System.currentTimeMillis()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, TextUtil.parse("&cNie możesz się tak szybko połączyć!"));
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CompletableFuture.supplyAsync(() -> this.playerSyncDataRepository.load(player.getUniqueId()))
                .thenAcceptAsync(playerSyncData -> {
                    if (playerSyncData == null) {
                        return;
                    }

                    this.flameDispatcher.dispatch(() -> {
                        World world = Bukkit.getWorld("world");
                        Location spawnLocation;
                        if (world == null) {
                            spawnLocation = LocationUtil.deserialize(playerSyncData.getSerializedLocation());
                        }
                        else {
                            Location worldSpawnLocation = world.getSpawnLocation().clone();
                            spawnLocation = worldSpawnLocation.toCenterLocation();
                        }

                        PlayerSyncDataApplicator.apply(player, playerSyncData, spawnLocation);
                    });


                })
                .exceptionally(throwable -> {

                    throwable.printStackTrace();
                    player.kick(TextUtil.parse("&cWystąpił krytyczny błąd podczas ładowania danych, zgłoś to administracji!"));
                    return null;

                });


    }



    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        this.lastConnections.put(player.getUniqueId(), System.currentTimeMillis());
        this.flameDispatcher.dispatchAsync(() -> {
            this.playerSyncDataRepository.save(PlayerSyncDataFactory.create(player));
        });
    }

}
