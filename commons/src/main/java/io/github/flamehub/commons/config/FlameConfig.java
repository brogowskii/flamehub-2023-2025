package io.github.flamehub.commons.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FlameConfig {

  @JsonIgnore
  public FlameConfigProperties getProperties() {
    return getClass().getAnnotation(FlameConfigProperties.class);
  }

}
