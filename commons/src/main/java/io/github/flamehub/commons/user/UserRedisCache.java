package io.github.flamehub.commons.user;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.redis.cache.RedisCache;
import io.github.flamehub.commons.util.CompletableFutures;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.redisson.api.RLock;

public class UserRedisCache<U extends User> extends RedisCache<UUID, U> implements UserCache<U> {

  protected final UserRepository<U> userRepository;
  protected final Map<String, UUID> uuidByName;

  public UserRedisCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final Class<U> type,
      final String namespace,
      final UserRepository<U> userRepository
  ) {
    super(redisMessenger, redisService, type, namespace, false);
    this.userRepository = userRepository;
    uuidByName = new ConcurrentHashMap<>();
  }


  public void update(final UUID uuid, final Consumer<U> entity) {
    final RLock lock = cachedMap.getReadWriteLock(uuid.toString()).writeLock();
    lock.lock();
    try {

      final U user = findByUniqueId(uuid);
      entity.accept(user);
      put(uuid, user);

      CompletableFuture.runAsync(() -> userRepository.save(user))
          .exceptionally(CompletableFutures::delegateCaughtException);
    } finally {
      lock.forceUnlock();
    }
  }

  public void updateName(final U user, final String newName) {
    uuidByName.remove(user.getName().toLowerCase());
    user.setName(newName);
  }

  public void add(final U user) {
    put(user.getUniqueId(), user);
    uuidByName.put(user.getName().toLowerCase(), user.getUniqueId());
  }

  public void remove(final User user) {
    remove(user.getUniqueId());
    uuidByName.remove(user.getName().toLowerCase());
  }

  @Override
  public U findByUniqueId(final UUID uniqueId) {
    final U user = get(uniqueId);
    if (user != null) {
      return user;
    }

    final U fetchedUser = userRepository.load(uniqueId);
    if (fetchedUser == null) {
      return null;
    }

    uuidByName.put(fetchedUser.getName().toLowerCase(), uniqueId);
    put(uniqueId, fetchedUser);
    return fetchedUser;
  }

  @Override
  public U findByName(final String name) {
    final UUID retrievedUniqueId = uuidByName.get(name.toLowerCase());
    if (retrievedUniqueId != null) {
      return findByUniqueId(retrievedUniqueId);
    }

    return userRepository.loadIgnoreCase("name", name);
  }

  @Override
  public Collection<U> values() {
    return Collections.unmodifiableCollection(cachedMap.values());
  }
}
