package io.github.flamehub.commons.redis;

import java.util.List;

public class RedisRepository<E> {

  protected final RedisService redisService;
  protected final Class<E> entityClass;

  public RedisRepository(RedisService redisService, Class<E> entityClass) {
    this.redisService = redisService;
    this.entityClass = entityClass;
  }

  public E load(String id) {
    return redisService.load(getRedisEntity().mapName(), id, entityClass);
  }

  public boolean save(String id, E entity) {
    return redisService.save(getRedisEntity().mapName(), id, entity);
  }

  public void delete(String id) {
    redisService.remove(getRedisEntity().mapName(), id);
  }

  public List<E> loadAll() {
    return redisService.load(getRedisEntity().mapName(), entityClass);
  }

  RedisEntity getRedisEntity() {
    return entityClass.getAnnotation(RedisEntity.class);
  }

}
