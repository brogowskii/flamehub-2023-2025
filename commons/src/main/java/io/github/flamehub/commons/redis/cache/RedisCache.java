package io.github.flamehub.commons.redis.cache;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import java.util.Collection;
import java.util.Collections;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.api.options.LocalCachedMapOptions.CacheProvider;
import org.redisson.api.options.LocalCachedMapOptions.EvictionPolicy;
import org.redisson.api.options.LocalCachedMapOptions.ReconnectionStrategy;
import org.redisson.api.options.LocalCachedMapOptions.SyncStrategy;

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
    this.cachedMap = redisService.getClient().getLocalCachedMap(
        LocalCachedMapOptions.<String, V>name(name)
            .cacheProvider(CacheProvider.CAFFEINE)
            .evictionPolicy(EvictionPolicy.LRU)
            .syncStrategy(SyncStrategy.UPDATE)
            .reconnectionStrategy(ReconnectionStrategy.LOAD)
    );

  }

  public void put(final K key, final V value) {
    final String keyString = key.toString();
    cachedMap.put(keyString, value);
  }

  public V get(final K key) {
    return get(key.toString());
  }

  public V get(final String key) {
    return cachedMap.get(key);
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