package io.github.flamehub.commons.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesService {

  private final Map<String, List<String>> messageMap = new HashMap<>();

  public List<String> getMessages(final String path) {
    final List<String> messages = messageMap.getOrDefault(path, new ArrayList<>());
    if (messages.isEmpty()) {
      return Collections.singletonList(path + " == empty");
    }

    return messages;
  }

  public String getMessage(final String path) {
    final List<String> messages = getMessages(path);
    final String message = messages.get(0);
    if (message == null || message.isEmpty()) {
      return path + " == empty";
    }

    return message;
  }

  public Message message(final String path) {
    return MessageBuilder.of().add(getMessages(path));
  }


  public Map<String, List<String>> getMessageMap() {
    return messageMap;
  }

}