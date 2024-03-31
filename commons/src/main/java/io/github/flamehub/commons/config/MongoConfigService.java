package io.github.flamehub.commons.config;

import java.lang.reflect.Field;
import java.util.function.Function;

public final class MongoConfigService {

    private final MongoConfigRepository mongoConfigRepository;

    public MongoConfigService(MongoConfigRepository mongoConfigRepository) {
        this.mongoConfigRepository = mongoConfigRepository;
    }

    public <C extends MongoConfig> void save(C configEntity) {
        this.mongoConfigRepository.save(configEntity);
    }

    public <C extends MongoConfig> C find(Class<C> configClass, String id) {
        return  this.mongoConfigRepository.load(configClass, id);
    }

    public <C extends MongoConfig> C findOrCreate(Class<C> configClass, String id, Function<String, C> function) {
        C config = this.mongoConfigRepository.load(configClass, id);
        if (config == null) {
            config = function.apply(id);
        }

        save(config);
        return config;
    }

    public <C extends MongoConfig> void refresh(Class<C> clazz, C config) throws IllegalAccessException {
        C freshConfig = find(clazz, config.getId());
        if (freshConfig != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(config, field.get(freshConfig));
            }
        }
    }


}
