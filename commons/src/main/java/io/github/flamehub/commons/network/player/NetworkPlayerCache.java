package io.github.flamehub.commons.network.player;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.redisson.api.RMap;

public final class NetworkPlayerCache {

  private final static String MAP_NAME = "network_players";

  private final Map<String, NetworkPlayer> networkPlayersByName = new ConcurrentHashMap<>();
  private final Map<UUID, NetworkPlayer> networkPlayersByUniqueId = new ConcurrentHashMap<>();

  private final RedisService redisService;
  private final RedisMessenger redisMessenger;

  public NetworkPlayerCache(final RedisService redisService, final RedisMessenger redisMessenger) {
    this.redisService = redisService;
    this.redisMessenger = redisMessenger;
  }

  public void add(final NetworkPlayer networkPlayer) {
    networkPlayersByName.put(networkPlayer.getName().toLowerCase(), networkPlayer);
    networkPlayersByUniqueId.put(networkPlayer.getUniqueId(), networkPlayer);
  }

  public void remove(final NetworkPlayer networkPlayer) {
    networkPlayersByName.remove(networkPlayer.getName().toLowerCase());
    networkPlayersByUniqueId.remove(networkPlayer.getUniqueId());
  }

  public void delete(final NetworkPlayer networkPlayer) {
    redisService.getClient().getMap(MAP_NAME).remove(networkPlayer.getUniqueId());
    redisMessenger.publish("network_players",
        new NetworkPlayerDelete(networkPlayer.getUniqueId()));
  }

  public NetworkPlayer findByUniqueId(final UUID uniqueId) {
    return networkPlayersByUniqueId.get(uniqueId);
  }

  public NetworkPlayer findByName(final String name) {
    return networkPlayersByName.get(name.toLowerCase());
  }

  public void save(final NetworkPlayer networkPlayer) {
    redisService.getClient().getMap(MAP_NAME).put(networkPlayer.getUniqueId(), networkPlayer);
    redisMessenger.publish("network_players",
        new NetworkPlayerUpdate(networkPlayer));
  }

  public void load() {
    final RMap<UUID, NetworkPlayer> map = redisService.getClient().getMap(MAP_NAME);
    final Collection<NetworkPlayer> values = map.values();
    for (final NetworkPlayer networkPlayer : values) {
      add(networkPlayer);
    }
  }

  public Collection<NetworkPlayer> values() {
    return new HashSet<>(networkPlayersByUniqueId.values());
  }

}