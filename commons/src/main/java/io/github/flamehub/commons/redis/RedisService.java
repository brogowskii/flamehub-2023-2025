package io.github.flamehub.commons.redis;

import io.github.flamehub.commons.json.JsonUtil;
import io.github.flamehub.commons.redis.codec.StringByteArrayCodec;
import io.github.flamehub.commons.redis.lock.RedisLock;
import io.github.flamehub.commons.redis.storage.RedisStorage;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import java.util.List;
import java.util.Map;

public final class RedisService {

  private final Long clientId;

  private final RedisClient client;
  private RedisStorage lockRedisStorage;

  public RedisService(String hostname, String password, int port) {
    RedisURI.Builder builder = RedisURI.builder();
    builder.withHost(hostname).withPort(port);
    if (!password.isEmpty()) {
      builder.withPassword(password.toCharArray());
    }

    this.client = RedisClient.create(builder.build());
    this.clientId = client.connect().sync().clientId();
    this.lockRedisStorage = retrieveStorage("locks");
  }

  public RedisLock retrieveLock(String key) {
    return new RedisLock(key, lockRedisStorage);
  }

  public RedisStorage retrieveStorage(String namespace) {
    return new RedisStorage(client.connect(new StringByteArrayCodec()), namespace);
  }

  public <T> T load(String mapName, String key, Class<T> type) {
    try (StatefulRedisConnection<String, String> connection = client.connect()) {
      String get = connection.sync().hget(mapName, key);
      return JsonUtil.DATABASE_GSON.fromJson(get, type);
    }
  }

  public <T> List<T> load(String mapName, Class<T> type) {
    try (StatefulRedisConnection<String, String> connection = client.connect()) {
      Map<String, String> get = connection.sync().hgetall(mapName);
      return get.values().stream()
          .map(s -> JsonUtil.DATABASE_GSON.fromJson(s, type))
          .toList();

    }
  }

  public <T> boolean save(String mapName, String key, T value) {
    try (StatefulRedisConnection<String, String> connection = client.connect()) {
      return connection.sync().hset(mapName, key, JsonUtil.DATABASE_GSON.toJson(value));
    }
  }

  public void remove(String mapName, String key) {
    try (StatefulRedisConnection<String, String> connection = client.connect()) {
      connection.sync().hdel(mapName, key);
    }
  }

  public long size(String mapName) {
    try (StatefulRedisConnection<String, String> connection = client.connect()) {
      return connection.sync()
          .hgetall(mapName)
          .size();
    }
  }

  public RedisClient getClient() {
    return client;
  }

  public Long getClientId() {
    return clientId;
  }
}
