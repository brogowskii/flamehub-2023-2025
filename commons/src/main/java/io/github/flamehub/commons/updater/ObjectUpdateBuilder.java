package io.github.flamehub.commons.updater;

import java.util.HashMap;
import java.util.Map;

public class ObjectUpdateBuilder {

  private Object id;
  private Map<String, Object> fieldValueMap;

  public ObjectUpdateBuilder() {
    this.fieldValueMap = new HashMap<>();
  }

  public static ObjectUpdateBuilder builder() {
    return new ObjectUpdateBuilder();
  }

  public ObjectUpdateBuilder id(Object id) {
    this.id = id;
    return this;
  }

  public ObjectUpdateBuilder fieldValue(String field, Object value) {
    if (fieldValueMap == null) {
      this.fieldValueMap = new HashMap<>();
    }

    fieldValueMap.put(field, value);
    return this;
  }

  public ObjectUpdateBuilder fieldValueMap(Map<String, Object> fieldValueMap) {
    this.fieldValueMap = fieldValueMap;
    return this;
  }

  public ObjectUpdate build() {
    return new ObjectUpdate(id, fieldValueMap);
  }
}