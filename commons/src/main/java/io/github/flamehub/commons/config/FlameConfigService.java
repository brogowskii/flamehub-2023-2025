package io.github.flamehub.commons.config;

import io.github.flamehub.commons.messenger.RedisMessenger;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public final class FlameConfigService {

  private final Map<String, FlameConfig> configInstancesByClassName = new ConcurrentHashMap<>();

  private final RedisMessenger redisMessenger;
  private final RemoteRepository remoteRepository;
  private final String remoteConfigUpdateChannel;

  public FlameConfigService(
      final RedisMessenger redisMessenger,
      final RemoteRepository remoteRepository,
      final String remoteConfigUpdateChannel) {
    this.redisMessenger = redisMessenger;
    this.remoteRepository = remoteRepository;
    this.remoteConfigUpdateChannel = remoteConfigUpdateChannel;
  }


  @NotNull
  public <CONFIG extends FlameConfig> CONFIG getOrCreate(final Class<CONFIG> clazz) {

    final Constructor<CONFIG> constructor;
    final CONFIG defaultConfig;
    try {
      constructor = clazz.getConstructor();
      defaultConfig = constructor.newInstance();
    } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException |
                   InvocationTargetException e) {
      throw new RuntimeException(e);
    }

    final FlameConfigProperties properties = defaultConfig.getProperties();
    if (properties == null) {
      throw new IllegalArgumentException("@FlameConfigProperties annotation not found");
    }

    final CONFIG loadedFromDb = remoteRepository.load(clazz);
    final CONFIG workingConfig;
    if (loadedFromDb == null) {
      System.out.println("Brak wpisu w DB, zapisuję domyślny config: " + clazz.getName());
      workingConfig = remoteRepository.save(defaultConfig);
    } else {
      System.out.println("Załadowano config z DB: " + clazz.getName());
      copyNonNullFields(loadedFromDb, defaultConfig);
      remoteRepository.save(defaultConfig);
      workingConfig = defaultConfig;
    }

    configInstancesByClassName.put(workingConfig.getClass().getName(), workingConfig);
    return workingConfig;
  }

  @SuppressWarnings("unchecked")
  public <CONFIG extends FlameConfig> void refreshAndBroadcast(final Class<CONFIG> configClazz)
      throws IllegalAccessException {

    final CONFIG config = (CONFIG) configInstancesByClassName.get(configClazz.getName());
    if (config == null) {
      throw new IllegalArgumentException("Config not found");
    }

    refresh(configClazz);
    redisMessenger.publish(remoteConfigUpdateChannel,
        new RemoteUpdate(config.getClass().getName()));
  }

  public <CONFIG extends FlameConfig> void refresh(final Class<CONFIG> clazz)
      throws IllegalAccessException {
    doRefreshFromDb(clazz, true);
  }

  @SuppressWarnings("unchecked")
  public <CONFIG extends FlameConfig> void save(final Class<CONFIG> configClazz) {
    final CONFIG config = (CONFIG) configInstancesByClassName.get(configClazz.getName());
    if (config == null) {
      throw new IllegalArgumentException("Config not found");
    }
    remoteRepository.save(config);
  }

  @SuppressWarnings("unchecked")
  private <CONFIG extends FlameConfig> void doRefreshFromDb(final Class<CONFIG> clazz,
      final boolean persistAfterMerge) throws IllegalAccessException {

    final CONFIG current = (CONFIG) configInstancesByClassName.get(clazz.getName());
    if (current == null) {
      throw new IllegalArgumentException("Config not found");
    }

    final CONFIG fresh = remoteRepository.load(clazz);
    if (fresh == null) {
      remoteRepository.save(current);
      return;
    }

    boolean changed = false;
    for (final Field field : clazz.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers())) {
        continue;
      }
      field.setAccessible(true);

      final Object dbValue = field.get(fresh);
      if (dbValue != null) {
        final Object old = field.get(current);
        if (!Objects.equals(old, dbValue)) {
          field.set(current, dbValue);
          changed = true;
        }
      } else {
        if (field.get(current) != null) {
          changed = true;
        }
      }
    }

    if (persistAfterMerge && changed) {
      remoteRepository.save(current);
    }
  }


  public String getRemoteConfigUpdateChannel() {
    return remoteConfigUpdateChannel;
  }

  public Map<String, FlameConfig> getConfigInstancesByClassName() {
    return configInstancesByClassName;
  }

  private boolean copyNonNullFields(final Object source, final Object target) {
    final Field[] fields = source.getClass().getDeclaredFields();

    boolean updated = false;
    for (final Field field : fields) {
      try {
        field.setAccessible(true);
        final Object value = field.get(source);

        if (value != null) {
          updated = true;
          field.set(target, value);
        }
      } catch (final IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return updated;
  }
}
