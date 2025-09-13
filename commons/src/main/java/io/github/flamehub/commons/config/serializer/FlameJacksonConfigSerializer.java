package io.github.flamehub.commons.config.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.flamehub.commons.config.FlameConfig;

public final class FlameJacksonConfigSerializer implements FlameConfigSerializer {

  private final ObjectMapper objectMapper;

  public FlameJacksonConfigSerializer(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public <CONFIG extends FlameConfig> String serialize(final CONFIG config) {
    try {
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <CONFIG extends FlameConfig> CONFIG deserialize(final String json,
      final Class<CONFIG> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
