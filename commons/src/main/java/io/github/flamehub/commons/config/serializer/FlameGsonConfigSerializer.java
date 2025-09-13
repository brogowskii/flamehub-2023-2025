package io.github.flamehub.commons.config.serializer;

import com.google.gson.Gson;
import io.github.flamehub.commons.config.FlameConfig;

public final class FlameGsonConfigSerializer implements FlameConfigSerializer {

  private final Gson gson;

  public FlameGsonConfigSerializer(final Gson gson) {
    this.gson = gson;
  }

  @Override
  public <CONFIG extends FlameConfig> String serialize(final CONFIG config) {
    return gson.toJson(config);
  }

  @Override
  public <CONFIG extends FlameConfig> CONFIG deserialize(
      final String json,
      final Class<CONFIG> clazz
  ) {
    return gson.fromJson(json, clazz);
  }
}
