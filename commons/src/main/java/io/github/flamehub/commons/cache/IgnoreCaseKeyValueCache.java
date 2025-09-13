package io.github.flamehub.commons.cache;

public class IgnoreCaseKeyValueCache<V> extends KeyValueCache<String, V> {

  public IgnoreCaseKeyValueCache(final boolean concurrent) {
    super(concurrent);
  }

  @Override
  public V findByKey(final String key) {
    if (key == null || key.isEmpty()) {
      return null;
    }
    return cache.get(key.toLowerCase());
  }

  @Override
  public void add(final String key, final V value) {
    cache.put(key.toLowerCase(), value);
  }

  @Override
  public void remove(final String key) {
    cache.remove(key.toLowerCase());
  }
}
