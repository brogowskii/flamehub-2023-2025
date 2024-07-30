package io.github.flamehub.commons.config;

import io.github.flamehub.commons.config.serializer.FlameConfigSerializer;
import io.github.flamehub.commons.messenger.RedisMessenger;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public final class FlameConfigService {

  private final Map<String, FlameConfig> configInstancesByClassName = new ConcurrentHashMap<>();

  private final RedisMessenger redisMessenger;
  private final RemoteRepository remoteRepository;
  private final FlameConfigSerializer flameConfigSerializer;
  private final String remoteConfigUpdateChannel;

  public FlameConfigService(
      final RedisMessenger redisMessenger,
      final RemoteRepository remoteRepository,
      final FlameConfigSerializer flameConfigSerializer, String remoteConfigUpdateChannel
  ) {
    this.redisMessenger = redisMessenger;
    this.remoteRepository = remoteRepository;
    this.flameConfigSerializer = flameConfigSerializer;
    this.remoteConfigUpdateChannel = remoteConfigUpdateChannel;
  }

  @NotNull
  public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> CONFIG getOrCreate(
      final File dataFolder,
      final CLAZZ clazz
  ) {

    Constructor<CONFIG> constructor;
    CONFIG config;
    try {
      constructor = clazz.getConstructor();
      config = constructor.newInstance();
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
             InvocationTargetException e) {
      throw new RuntimeException(e);
    }

    final FlameConfigProperties properties = config.getProperties();
    if (properties == null) {
      throw new IllegalArgumentException("@FlameConfigProperties annotation not found");
    }

    final boolean firstCreate = createFile(config, dataFolder, properties.name());

    CONFIG newConfig;
    final File file = new File(dataFolder, properties.name());
    final EnableRemote remote = config.getRemote();
    if (remote != null && firstCreate) {
      System.out.println(
          "Pierwsze utworzenie pliku konfiguracyjnego, próbuje załadować z bazy danych, w innym wypadku zapisuje do db! Plik: "
              + file.getName());
      final CONFIG loadedConfig = this.remoteRepository.load(clazz);
      if (loadedConfig != null) {
        newConfig = loadedConfig;
      } else {
        newConfig = this.remoteRepository.save(config);
      }
    } else {

      try {
        System.out.println(
            "Plik konfiguracyjny istnieje, próbuje załadować z pliku! Plik: " + file.getName());
        String fileContent = readFileContent(file);
        newConfig = this.flameConfigSerializer.deserialize(fileContent, clazz);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    }

    if (newConfig == null) {
      throw new IllegalArgumentException("Config is still null");
    }

    copyNonNullFields(newConfig, config);
    config.setDataFolder(dataFolder);
    this.configInstancesByClassName.put(config.getClass().getName(), config);

    this.saveLocally(config.getClass());
    return config;

  }

  public boolean copyNonNullFields(
      final Object source,
      final Object target
  ) {
    final Field[] fields = source.getClass().getDeclaredFields();

    boolean updated = false;
    for (Field field : fields) {
      try {
        field.setAccessible(true);
        Object value = field.get(source);

        if (value != null) {
          updated = true;
          field.set(target, value);

          System.out.println("Updated field: " + field.getName() + " with value: " + value);
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return updated;
  }


  @SuppressWarnings("unchecked")
  public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> void update(
      final CLAZZ configClazz)
      throws IllegalAccessException {

    final CONFIG config = (CONFIG) this.configInstancesByClassName.get(configClazz.getName());
    if (config == null) {
      throw new IllegalArgumentException("Config not found");
    }

    final FlameConfigProperties properties = config.getProperties();
    if (properties == null) {
      throw new IllegalArgumentException("@FlameConfigProperties annotation not found");
    }

    refresh(configClazz, true);
    if (config.getRemote() != null) {
      CompletableFuture.supplyAsync(() -> this.remoteRepository.save(config))
          .thenAcceptAsync(savedConfig -> {
            System.out.println("themacceptasync send packet");
            this.redisMessenger.publish(
                this.remoteConfigUpdateChannel,
                new RemoteUpdate(savedConfig.getClass().getName())
            );
          })
          .exceptionally(e -> {
            throw new RuntimeException(e);
          });
    }

  }

  @SuppressWarnings("unchecked")
  public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> void saveLocally(
      final CLAZZ configClazz) {
    final CONFIG flameConfig = (CONFIG) this.configInstancesByClassName.get(configClazz.getName());
    if (flameConfig == null) {
      throw new IllegalArgumentException("Config not found");
    }

    final FlameConfigProperties properties = flameConfig.getProperties();
    if (properties == null) {
      throw new IllegalArgumentException("@FlameConfigProperties annotation not found");
    }

    saveFile(flameConfig, flameConfig.getDataFolder(), properties.name());
  }

  public <CONFIG extends FlameConfig> void saveFile(
      final CONFIG config,
      final File dataFolder,
      final String fileName
  ) {
    final Path filePath = dataFolder.toPath().resolve(fileName);
    try {
      Files.createDirectories(filePath.getParent());
      final String serialize = this.flameConfigSerializer.serialize(config);
      Files.writeString(filePath, serialize, StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public <CONFIG extends FlameConfig> boolean createFile(
      final CONFIG config,
      final File dataFolder,
      final String fileName
  ) {
    boolean firstCreate = false;
    final Path filePath = dataFolder.toPath().resolve(fileName);
    try {
      Files.createDirectories(filePath.getParent());
      if (!Files.exists(filePath)) {
        final String serialize = this.flameConfigSerializer.serialize(config);
        Files.writeString(filePath, serialize, StandardOpenOption.CREATE_NEW,
            StandardOpenOption.WRITE);
        firstCreate = true;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return firstCreate;
  }

  public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> void refresh(final CLAZZ clazz)
      throws IllegalAccessException {
    refresh(clazz, false);
  }

  public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> void refreshLocally(
      final CLAZZ clazz) throws IllegalAccessException {
    refresh(clazz, true);
  }


  @SuppressWarnings("unchecked")
  public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> void refresh(
      final CLAZZ clazz,
      boolean forceFromFile
  ) throws IllegalAccessException {

    final CONFIG config = (CONFIG) this.configInstancesByClassName.get(clazz.getName());
    if (config == null) {
      throw new IllegalArgumentException("Config not found");
    }

    final CONFIG freshConfig;
    if (config.getRemote() != null && !forceFromFile) {
      freshConfig = this.remoteRepository.load(clazz);
      saveFile(freshConfig, config.getDataFolder(), config.getProperties().name());
    } else {
      final File file = new File(config.getDataFolder(), config.getProperties().name());
      try {
        String fileContent = readFileContent(file);
        freshConfig = this.flameConfigSerializer.deserialize(fileContent, clazz);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    if (freshConfig != null) {
      for (Field field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
        field.set(config, field.get(freshConfig));
      }
    }

  }

  public String readFileContent(File file) throws IOException {
    StringBuilder content = new StringBuilder();
    try (Reader reader = new FileReader(file)) {
      char[] buffer = new char[1024];
      int numCharsRead;
      while ((numCharsRead = reader.read(buffer)) != -1) {
        content.append(buffer, 0, numCharsRead);
      }
    }
    return content.toString();
  }

  public String getRemoteConfigUpdateChannel() {
    return remoteConfigUpdateChannel;
  }

  public Map<String, FlameConfig> getConfigInstancesByClassName() {
    return configInstancesByClassName;
  }
}
