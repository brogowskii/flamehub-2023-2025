package io.github.flamehub.commons.user;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.redis.cache.RedisCache;
import io.github.flamehub.commons.util.CompletableFutures;
import io.github.flamehub.commons.util.ThrowingConsumer;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UserRedisCache<U extends User> extends RedisCache<UUID, U> implements UserCache<U> {

  protected final UserRepository<U> userRepository;
  protected final Map<String, UUID> uuidByName;

  public UserRedisCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final Class<U> type,
      final String namespace,
      final int cacheSize,
      final Duration expireAfterAccess,
      final UserRepository<U> userRepository) {
    super(redisMessenger, redisService, type, namespace, cacheSize, expireAfterAccess);
    this.userRepository = userRepository;
    this.uuidByName = new ConcurrentHashMap<>();
  }


  public CompletableFuture<U> mutate(
      final UUID uuid, final ThrowingConsumer<U, Exception> mutator) {
    return supplyLocked(
        uuid,
        () -> {
          final U user = findByUniqueId(uuid);
          if (user == null) {
            throw new UserException(
                "User with UUID %s not found in redis-cache nor database for mutation"
                    .formatted(uuid));
          }

          mutator.accept(user);
          set(uuid, user);
          CompletableFuture.runAsync(() -> userRepository.save(user))
              .exceptionally(CompletableFutures::delegateCaughtException);
          return user;
        });
  }

  public void add(final U user) {
    set(user.getUniqueId(), user);
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
    set(uniqueId, fetchedUser);
    return fetchedUser;
  }

  @Override
  public U findByName(final String name) {
    final UUID retrievedUniqueId = uuidByName.get(name.toLowerCase());
    if (retrievedUniqueId != null) {
      System.out.println("uuid found, returning user");
      return findByUniqueId(retrievedUniqueId);
    }

    return userRepository.loadIgnoreCase("name", name);
  }

  @Override
  public Collection<U> values() {
    return Collections.unmodifiableCollection(localCache.values());
  }
}
