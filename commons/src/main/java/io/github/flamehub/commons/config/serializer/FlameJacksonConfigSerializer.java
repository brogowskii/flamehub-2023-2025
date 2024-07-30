package io.github.flamehub.commons.config.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.flamehub.commons.config.FlameConfig;

public final class FlameJacksonConfigSerializer implements FlameConfigSerializer {

  private final ObjectMapper objectMapper;

  public FlameJacksonConfigSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public <CONFIG extends FlameConfig> String serialize(CONFIG config) {
    try {
      return this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <CONFIG extends FlameConfig, CLAZZ extends Class<CONFIG>> CONFIG deserialize(String json,
      CLAZZ clazz) {
    try {
      return this.objectMapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
