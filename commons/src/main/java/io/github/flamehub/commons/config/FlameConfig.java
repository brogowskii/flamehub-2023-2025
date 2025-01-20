package io.github.flamehub.commons.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.File;

public class FlameConfig {

  private transient File dataFolder;

  @JsonIgnore
  public File getDataFolder() {
    return dataFolder;
  }

  public void setDataFolder(File dataFolder) {
    this.dataFolder = dataFolder;
  }

  @JsonIgnore
  public FlameConfigProperties getProperties() {
    return getClass().getAnnotation(FlameConfigProperties.class);
  }


  @JsonIgnore
  public EnableRemote getRemote() {
    return getClass().getAnnotation(EnableRemote.class);
  }

}
