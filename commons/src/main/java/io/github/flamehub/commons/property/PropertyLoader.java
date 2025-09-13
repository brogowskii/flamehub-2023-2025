package io.github.flamehub.commons.property;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

  private final Properties properties;

  public PropertyLoader(final String propertiesFilePath) {
    properties = new Properties();
    try (final InputStream input = new FileInputStream(propertiesFilePath)) {
      properties.load(input);
      System.out.println("Loaded properties from " + propertiesFilePath);
    } catch (final IOException ex) {
      System.out.println("Failed to load properties file from " + propertiesFilePath);
      ex.printStackTrace();
    }
  }

  public String getProperty(final String key) {
    return properties.getProperty(key);
  }
}