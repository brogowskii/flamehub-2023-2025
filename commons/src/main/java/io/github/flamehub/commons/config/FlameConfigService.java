package io.github.flamehub.commons.config;

import io.github.flamehub.commons.config.serializer.FlameConfigSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FlameConfigService {

    private final Map<String, FlameConfig> configInstancesByClassName = new ConcurrentHashMap<>();

    private final RemoteRepository remoteRepository;
    private final FlameConfigSerializer flameConfigSerializer;

    public FlameConfigService(
            final RemoteRepository remoteRepository,
            final FlameConfigSerializer flameConfigSerializer
    ) {
        this.remoteRepository = remoteRepository;
        this.flameConfigSerializer = flameConfigSerializer;
    }

    public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> CONFIG getOrCreate(
            final File dataFolder,
            final String fileName,
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

        createFileAndSave(config, dataFolder, fileName, false);
        final EnableRemote remote = config.getRemote();
        if (remote != null) {
            final CONFIG loadedConfig = this.remoteRepository.load(config, clazz);

            if (loadedConfig == null) {
                config = this.remoteRepository.save(config);
            }
            else {
                config = loadedConfig;
            }
        }

        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }

        this.configInstancesByClassName.put(config.getClass().getName(), config);
        return config;

    }

    public <CONFIG extends FlameConfig> void save(final CONFIG config, final File dataFolder) {
        final FlameConfigProperties properties = config.getProperties();
        if (properties == null) {
            throw new IllegalArgumentException("@FlameConfigProperties annotation not found");
        }

        createFileAndSave(config, dataFolder, properties.name(), true);
        if (config.getRemote() != null) {
            this.remoteRepository.save(config);
        }



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

    public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> void refresh(final CLAZZ clazz) {
        CONFIG cfg = (CONFIG) this.configInstancesByClassName.get(clazz.getName());
        if (config == null) {
            throw new IllegalArgumentException("Config not found");
        }

        this.remoteRepository.refresh(clazz, config);
    }

    public Map<String, ? extends FlameConfig> getConfigInstancesByClassName() {
        return configInstancesByClassName;
    }
}
