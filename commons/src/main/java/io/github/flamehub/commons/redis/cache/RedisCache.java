package io.github.flamehub.commons.redis.cache;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.options.LocalCachedMapOptions;

public abstract class RedisCache<K, V> {

  protected final Class<V> type;
  protected final RedisMessenger redisMessenger;
  protected final RLocalCachedMap<String, V> cachedMap;
  protected final RedisService redisService;

  protected RedisCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final Class<V> type,
      final String name) {
    this.type = type;
    this.redisService = redisService;
    this.redisMessenger = redisMessenger;
    cachedMap = redisService.getClient().getLocalCachedMap(
        LocalCachedMapOptions.<String, V>name(name)
            .cacheProvider(LocalCachedMapOptions.CacheProvider.CAFFEINE)
            .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.NONE)
            .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
            .cacheSize(0)
            .timeToLive(Duration.ZERO)
            .maxIdle(Duration.ZERO)
            .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.LOAD)
            .storeMode(LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS)
            .storeCacheMiss(true)
    );

  }

  public void put(final K key, final V value) {
    final String keyString = key.toString();
    cachedMap.put(keyString, value);
  }

  public void fastPut(final K key, final V value) {
    final String keyString = key.toString();
    cachedMap.fastPut(keyString, value);
  }

  public V get(final K key) {
    return cachedMap.get(key.toString());
  }

  public V getOrCreate(final K key, final V value) {
    final String keyString = key.toString();
    if (cachedMap.containsKey(keyString)) {
      return cachedMap.get(keyString);
    }
    cachedMap.put(keyString, value);
    return value;
  }

  public void remove(final K key) {
    cachedMap.remove(key.toString());
  }

  protected void clear() {
    cachedMap.clear();
  }

  public Collection<V> values() {
    return Collections.unmodifiableCollection(cachedMap.values());
  }

}