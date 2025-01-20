package io.github.flamehub.commons.redis.storage;

import io.github.flamehub.commons.json.JsonUtil;
import io.github.flamehub.commons.util.ThrowingSupplier;
import io.lettuce.core.api.StatefulRedisConnection;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class RedisStorage {

  private final StatefulRedisConnection<String, byte[]> connection;
  private final String namespace;

  public RedisStorage(final StatefulRedisConnection<String, byte[]> connection,
      final String namespace) {
    this.connection = connection;
    this.namespace = namespace;
  }

  private <T, E extends Exception> T performSafely(final ThrowingSupplier<T, E> action,
      final Supplier<String> message) throws StorageException {
    try {
      return action.get();
    } catch (final Exception exception) {
      throw new StorageException(message.get(), exception);
    }
  }

  public boolean set(final String key, final Object value) {
    return performSafely(() -> {
      final String json = JsonUtil.GSON.toJson(value);
      if (json == null) {
        return null;
      }

      final byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
      return connection.sync().hset(namespace, key, bytes);
    }, () -> "Failed to put %s at %s in redis-cache".formatted(value, key));
  }

  public <T> T get(final String key, final Class<T> type) {
    return performSafely(() -> {
      final byte[] bytes = connection.sync().hget(namespace, key);
      if (bytes == null) {
        return null;
      }

      final String json = new String(bytes, StandardCharsets.UTF_8);
      return JsonUtil.GSON.fromJson(json, type);
    }, () -> "Failed to get value from Redis");
  }

  public <T> T get(final String key, final Type type) {
    return performSafely(() -> {
      byte[] bytes = connection.sync().hget(namespace, key);
      if (bytes == null) {
        return null;
      } else {
        String json = new String(bytes, StandardCharsets.UTF_8);
        return JsonUtil.GSON.fromJson(json, type);
      }
    }, () -> "Failed to get value from Redis");
  }

  public boolean remove(final String key) throws StorageException {
    return performSafely(() -> connection.sync().hdel(namespace, key) > 0,
        () -> "Failed to delete %s from redis-cache".formatted(key));
  }

  public <T> Map<String, T> getAll(final Class<T> type) {
    return performSafely(() -> {
      Map<String, byte[]> entries = connection.sync().hgetall(namespace);
      if (entries == null || entries.isEmpty()) {
        return Map.of();
      }
      return entries.entrySet()
          .stream()
          .collect(
              Collectors.toMap(
                  Map.Entry::getKey,
                  entry ->
                      JsonUtil.GSON.fromJson(new String(
                              entry.getValue(),
                              StandardCharsets.UTF_8),
                          type)
              ));
    }, () -> "Failed to get all values from Redis");
  }

}
