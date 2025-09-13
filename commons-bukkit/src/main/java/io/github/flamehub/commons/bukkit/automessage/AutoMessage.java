package io.github.flamehub.commons.bukkit.automessage;

import java.io.Serializable;
import java.util.List;

public final class AutoMessage implements Serializable {

  private final List<String> messages;

  public AutoMessage(final List<String> messages) {
    this.messages = messages;
  }

  public List<String> getMessages() {
    return messages;
  }

}
