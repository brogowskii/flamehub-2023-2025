package io.github.flamehub.commons.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {

  private final Map<String, Object> placeholders = new HashMap<>();
  private final List<String> messages = new ArrayList<>();

  public static Message from(String message) {
    return new Message().add(message);
  }

  public static Message from(List<String> messages) {
    return new Message().add(messages);
  }

  public static Message from(String... messages) {
    return new Message().add(messages);
  }

  public Message add(String message) {
    messages.add(message);
    return this;
  }

  public Message add(List<String> messages) {
    this.messages.addAll(messages);
    return this;
  }

  public Message add(String... messages) {
    Collections.addAll(this.messages, messages);
    return this;
  }


  public Message with(String from, Object to) {
    if (to == null) {
      placeholders.put(from, from + "=null");
      return this;
    }
    placeholders.put(from, to);
    return this;
  }

  public List<String> apply() {
    return !placeholders.isEmpty() ? PlaceholderReplacer.replacePlaceholders(messages, placeholders)
        : messages;
  }

  public String applyFirst() {
    return apply().get(0);
  }


}
