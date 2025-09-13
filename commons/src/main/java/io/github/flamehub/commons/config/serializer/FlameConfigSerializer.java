package io.github.flamehub.commons.config.serializer;

import io.github.flamehub.commons.config.FlameConfig;

public interface FlameConfigSerializer {

  <CONFIG extends FlameConfig> String serialize(final CONFIG config);

  <CONFIG extends FlameConfig> CONFIG deserialize(
      final String json,
      final Class<CONFIG> clazz
  );

}
