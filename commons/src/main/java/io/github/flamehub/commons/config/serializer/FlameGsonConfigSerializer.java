package io.github.flamehub.commons.config.serializer;

import com.google.gson.Gson;
import io.github.flamehub.commons.config.FlameConfig;

public final class FlameGsonConfigSerializer implements FlameConfigSerializer {

  private final Gson gson;

  public FlameGsonConfigSerializer(Gson gson) {
    this.gson = gson;
  }

  @Override
  public <CONFIG extends FlameConfig> String serialize(final CONFIG config) {
    return gson.toJson(config);
  }

  @Override
  public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> CONFIG deserialize(
      final String json,
      final CLAZZ clazz
  ) {
    return gson.fromJson(json, clazz);
  }
}
