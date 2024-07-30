package io.github.flamehub.commons.property;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

  private Properties properties;

  public PropertyLoader(String propertiesFilePath) {
    properties = new Properties();
    try (InputStream input = new FileInputStream(propertiesFilePath)) {
      properties.load(input);
      System.out.println("Loaded properties from " + propertiesFilePath);
    } catch (IOException ex) {
      System.out.println("Failed to load properties file from " + propertiesFilePath);
      ex.printStackTrace();
    }
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }
}