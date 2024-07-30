package io.github.flamehub.commons.cache;

public class IgnoreCaseKeyValueCache<V> extends KeyValueCache<String, V> {

  public IgnoreCaseKeyValueCache(boolean concurrent) {
    super(concurrent);
  }

  @Override
  public V findByKey(String key) {
    return this.cache.get(key.toLowerCase());
  }

  @Override
  public void add(String key, V value) {
    this.cache.put(key.toLowerCase(), value);
  }

  @Override
  public void remove(String key) {
    this.cache.remove(key.toLowerCase());
  }
}
