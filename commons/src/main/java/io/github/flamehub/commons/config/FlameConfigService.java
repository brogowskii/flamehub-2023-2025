package io.github.flamehub.commons.config;

import io.github.flamehub.commons.config.serializer.FlameConfigSerializer;
import io.github.flamehub.commons.messenger.RedisMessenger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class FlameConfigService {

    public final static String REMOTE_CONFIG_UPDATE_CHANNEL = "remote_config_update";

    private final Map<String, FlameConfig> configInstancesByClassName = new ConcurrentHashMap<>();

    private final RedisMessenger redisMessenger;
    private final RemoteRepository remoteRepository;
    private final FlameConfigSerializer flameConfigSerializer;

    public FlameConfigService(
            final RedisMessenger redisMessenger,
            final RemoteRepository remoteRepository,
            final FlameConfigSerializer flameConfigSerializer
    ) {
        this.redisMessenger = redisMessenger;
        this.remoteRepository = remoteRepository;
        this.flameConfigSerializer = flameConfigSerializer;
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
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        final FlameConfigProperties properties = config.getProperties();
        if (properties == null) {
            throw new IllegalArgumentException("@FlameConfigProperties annotation not found");
        }

        createFileAndSave(config, dataFolder, properties.name(), false);
        final EnableRemote remote = config.getRemote();
        if (remote != null) {
            final CONFIG loadedConfig = this.remoteRepository.load(clazz);

            if (loadedConfig == null) {
                config = this.remoteRepository.save(config);
            }
            else {
                config = loadedConfig;
            }
        }
        else {
            final File file = new File(dataFolder, properties.name());
            try {
                String fileContent = readFileContent(file);
                config = this.flameConfigSerializer.deserialize(fileContent, clazz);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        if (config == null) {
            throw new IllegalArgumentException("Config is still null");
        }

        this.configInstancesByClassName.put(config.getClass().getName(), config);
        config.setDataFolder(dataFolder);
        return config;

    }

    @SuppressWarnings("unchecked")
    public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> void update(final CLAZZ configClazz)
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
                        this.redisMessenger.publish(
                                REMOTE_CONFIG_UPDATE_CHANNEL,
                                new RemoteUpdate(savedConfig.getClass().getName())
                        );
                    })
                    .exceptionally(e -> {
                        throw new RuntimeException(e);
                    });
        }

    }

    public <CONFIG extends FlameConfig> void saveLocally(final CONFIG config) {
        final FlameConfigProperties properties = config.getProperties();
        if (properties == null) {
            throw new IllegalArgumentException("@FlameConfigProperties annotation not found");
        }

        createFileAndSave(config, config.getDataFolder(), properties.name(), true);
    }

    public <CONFIG extends FlameConfig> void createFileAndSave(
            final CONFIG config,
            final File dataFolder,
            final String fileName,
            final boolean overwrite
    ) {

        final File file = new File(dataFolder, fileName);
        try {
            if (overwrite || !file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();

                try (final FileWriter writer = new FileWriter(file)) {
                    final String serialize = this.flameConfigSerializer.serialize(config);
                    writer.write(serialize);
                }
            }

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

    public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> void refresh(final CLAZZ clazz) throws IllegalAccessException {
        refresh(clazz, false);
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
            createFileAndSave(freshConfig, config.getDataFolder(), config.getProperties().name(), true);
        }
        else {
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


    public Map<String, FlameConfig> getConfigInstancesByClassName() {
        return configInstancesByClassName;
    }
}
