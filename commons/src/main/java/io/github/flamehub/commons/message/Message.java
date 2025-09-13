package io.github.flamehub.commons.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class Message implements Serializable {

  @JsonIgnore
  private final transient Map<String, Object> placeholders = new HashMap<>();
  private final List<String> messages = new ArrayList<>();

  public Message() {
  }

  public static Message from(final String message) {
    return new Message().add(message);
  }

  public static Message from(final List<String> messages) {
    return new Message().add(messages);
  }

  public static Message from(final String... messages) {
    return new Message().add(messages);
  }

  public Message add(final String message) {
    messages.add(message);
    return this;
  }

  public Message add(final List<String> messages) {
    this.messages.addAll(messages);
    return this;
  }

  public Message add(final String... messages) {
    Collections.addAll(this.messages, messages);
    return this;
  }


  public Message with(final String from, final Object to) {
    if (to == null) {
      placeholders.put(from, from + "=null");
      return this;
    }
    placeholders.put(from, to);
    return this;
  }

  @JsonIgnore
  public List<String> apply() {
    return !placeholders.isEmpty() ? PlaceholderReplacer.replacePlaceholders(messages, placeholders)
        : messages;
  }

  @JsonIgnore
  public String applyFirst() {
    return apply().getFirst();
  }

  public List<String> getMessages() {
    return messages;
  }
}
