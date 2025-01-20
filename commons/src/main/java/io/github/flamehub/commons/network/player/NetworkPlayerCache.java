package io.github.flamehub.commons.network.player;

import io.github.flamehub.commons.json.JsonUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class NetworkPlayerCache {

  private final static String MAP_NAME = "network_players";

  private final Map<String, NetworkPlayer> networkPlayersByName = new ConcurrentHashMap<>();
  private final Map<UUID, NetworkPlayer> networkPlayersByUniqueId = new ConcurrentHashMap<>();

  private final RedisService redisService;
  private final RedisMessenger redisMessenger;

  public NetworkPlayerCache(RedisService redisService, RedisMessenger redisMessenger) {
    this.redisService = redisService;
    this.redisMessenger = redisMessenger;
  }

  public void add(NetworkPlayer networkPlayer) {
    networkPlayersByName.put(networkPlayer.getName().toLowerCase(), networkPlayer);
    networkPlayersByUniqueId.put(networkPlayer.getUniqueId(), networkPlayer);
  }

  public void remove(NetworkPlayer networkPlayer) {
    networkPlayersByName.remove(networkPlayer.getName().toLowerCase());
    networkPlayersByUniqueId.remove(networkPlayer.getUniqueId());
  }

  public void delete(NetworkPlayer networkPlayer) {
    redisService.remove(MAP_NAME, networkPlayer.getUniqueId().toString());
    redisMessenger.publish("network_players",
        new NetworkPlayerDelete(networkPlayer.getUniqueId()));
  }

  public NetworkPlayer findByUniqueId(UUID uniqueId) {
    return networkPlayersByUniqueId.get(uniqueId);
  }

  public NetworkPlayer findByName(String name) {
    return networkPlayersByName.get(name.toLowerCase());
  }

  public void save(NetworkPlayer networkPlayer) {
    redisService.save(MAP_NAME, networkPlayer.getUniqueId().toString(), networkPlayer);
    redisMessenger.publish("network_players",
        new NetworkPlayerUpdate(JsonUtil.DATABASE_GSON.toJson(networkPlayer)));
  }

  public void load() {
    for (NetworkPlayer networkPlayer : redisService.load(MAP_NAME, NetworkPlayer.class)) {
      add(networkPlayer);
    }
  }

  public Collection<NetworkPlayer> values() {
    return new HashSet<>(networkPlayersByUniqueId.values());
  }

}
