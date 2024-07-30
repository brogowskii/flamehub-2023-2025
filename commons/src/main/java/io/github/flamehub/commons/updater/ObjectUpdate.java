package io.github.flamehub.commons.updater;

import io.github.flamehub.commons.messenger.packet.Packet;
import java.util.Map;

public class ObjectUpdate implements Packet {

  private final Object id;
  private final Map<String, Object> fieldValueMap;

  public ObjectUpdate(Object id, Map<String, Object> fieldValueMap) {
    this.id = id;
    this.fieldValueMap = fieldValueMap;
  }

  public Object getId() {
    return id;
  }

  public Map<String, Object> getFieldValueMap() {
    return fieldValueMap;
  }
}
