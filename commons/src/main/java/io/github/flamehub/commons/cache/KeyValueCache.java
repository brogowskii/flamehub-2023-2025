package io.github.flamehub.commons.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueCache<K, V> {

  protected final Map<K, V> cache;

  public KeyValueCache(final boolean concurrent) {
    cache = concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
  }

  public V findByKey(final K key) {
    return cache.get(key);
  }

  public void add(final K key, final V value) {
    cache.put(key, value);
  }

  public void remove(final K key) {
    cache.remove(key);
  }

  public Collection<V> values() {
    return Collections.unmodifiableCollection(cache.values());
  }

  public Map<K, V> getCache() {
    return cache;
  }
}
