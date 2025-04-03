package io.github.flamehub.player.sync.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.flamehub.commons.redis.storage.RedisStorage;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public final class PlayerSyncDataFacade {

  private final RedisStorage playerSyncStorage;
  private final PlayerSyncDataRepository playerSyncDataRepository;

  public PlayerSyncDataFacade(
      final RedisStorage playerSyncStorage,
      final PlayerSyncDataRepository playerSyncDataRepository) {
    this.playerSyncStorage = playerSyncStorage;
    this.playerSyncDataRepository = playerSyncDataRepository;
  }


  public PlayerSyncData load(final UUID uniqueId) {
    PlayerSyncData syncData = playerSyncStorage.get(uniqueId.toString(), PlayerSyncData.class);
    if (syncData == null) {
      syncData = playerSyncDataRepository.load(uniqueId);
      playerSyncStorage.set(uniqueId.toString(), syncData);
    }
    return syncData;
  }

  public PlayerSyncData save(final PlayerSyncData syncData) {
    playerSyncStorage.set(syncData.getPlayerUniqueId().toString(), syncData);
    return playerSyncDataRepository.save(syncData);
  }
}
