package io.github.flamehub.commons.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueCache<K, V> {

    protected final Map<K, V> cache;

    public KeyValueCache(boolean concurrent) {
        this.cache = concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    public V findByKey(K key) {
        return this.cache.get(key);
    }

    public void add(K key, V value) {
        this.cache.put(key, value);
    }

    public void remove(K key) {
        this.cache.remove(key);
    }

    public Collection<V> values() {
        return Collections.unmodifiableCollection(this.cache.values());
    }

    public Map<K, V> getCache() {
        return cache;
    }
}
