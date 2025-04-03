package io.github.flamehub.commons.redis.cache;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.redis.codec.StringByteArrayCodec;
import io.github.flamehub.commons.redis.storage.RedisStorage;
import io.github.flamehub.commons.util.ThrowingSupplier;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RedisCache<K, V> {

  public final String pid;
  protected final String updatesTopic;
  protected final String lockTemplate;
  protected final Class<V> type;

  protected final RedisMessenger redisMessenger;
  protected final RedisStorage redisStorage;
  protected final RedisService redisService;
  protected final Map<String, V> localCache;

  protected RedisCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final Class<V> type,
      final String namespace,
      final int cacheSize,
      final Duration expireAfterAccess) {
    this.pid = UUID.nameUUIDFromBytes(
        ("cache:" + namespace + ":" + redisService.getClientId()).getBytes(UTF_8)).toString();
    this.updatesTopic = "cache:" + namespace + ":updates";
    this.lockTemplate = "cache:" + namespace + ":%s";
    this.type = type;
    this.redisService = redisService;
    this.redisMessenger = redisMessenger;
    this.redisStorage = new RedisStorage(
        redisService.getClient().connect(new StringByteArrayCodec()), namespace);
    this.localCache = new ConcurrentHashMap<>();

    redisMessenger.subscribe(updatesTopic, new CacheHandler(this));
  }

  public CompletableFuture<Void> performLocked(final K key, final Runnable task) {
    return redisService.retrieveLock(lockTemplate.formatted(key.toString())).execute(task);
  }

  public <T> CompletableFuture<T> supplyLocked(final K key,
      final ThrowingSupplier<T, Exception> supplier) {
    return redisService.retrieveLock(lockTemplate.formatted(key.toString())).supply(supplier);
  }

  public boolean set(final K key, final V value) {
    final String keyToString = key.toString();
    final boolean result = redisStorage.set(keyToString, value);
    localCache.put(keyToString, value);
    redisMessenger.publish(updatesTopic, new CacheUpdate(pid, keyToString));
    return result;
  }

  public V get(final K key) {
    final String keyToString = key.toString();
    V value = localCache.get(keyToString);
    if (value == null) {
      value = redisStorage.get(key.toString(), type);
      if (value != null) {
        localCache.put(keyToString, value);
      }
    }
    return value;
  }

  public boolean remove(final K key) {
    final String keyToString = key.toString();
    final boolean result = redisStorage.remove(keyToString);
    localCache.remove(keyToString);
    redisMessenger.publish(updatesTopic, new CacheUpdate(pid, keyToString));
    return result;
  }

//  protected void clear() {
//    redisStorage.clear();
//    localCache.invalidateAll();
//    redisService.publish(updatesTopic, new StorageUpdate("*"));
//  }

  void invalidateLocally(final String incomingPid, final String key) {
    if (pid.equals(incomingPid)) {
      return;
    }
    if ("*".equals(key)) {
      localCache.clear();
    } else {
      localCache.remove(key);
    }
  }

  public Collection<V> values() {
    return Collections.unmodifiableCollection(localCache.values());
  }

}