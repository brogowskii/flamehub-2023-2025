package io.github.flamehub.commons.bukkit.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public final class TextBuilder {

  private final List<String> text = new ArrayList<>();
  private final Map<String, Object> placeholders = new HashMap<>();

  public static TextBuilder builder() {
    return new TextBuilder();
  }

  public TextBuilder text(String message) {
    this.text.add(message);
    return this;
  }

  public TextBuilder text(List<String> message) {
    this.text.addAll(message);
    return this;
  }

  public TextBuilder text(String... message) {
    this.text.addAll(List.of(message));
    return this;
  }

  public TextBuilder placeholder(String from, Object to) {
    if (to == null) {
      this.placeholders.put(from, from + "=null");
      return this;
    }
    this.placeholders.put(from, to);
    return this;
  }

  public List<String> build() {
    if (!this.placeholders.isEmpty()) {
      List<String> replacedMessages = new ArrayList<>();

      for (String message : this.text) {
        String messageToReplace = message;

        for (Map.Entry<String, Object> entry : this.placeholders.entrySet()) {
          Object value = entry.getValue();
          String key = entry.getKey();
          messageToReplace = messageToReplace.replace(key, value.toString());
        }

        replacedMessages.add(messageToReplace);
      }

      return replacedMessages;
    }

    return this.text;
  }

  public List<Component> buildAsComponents() {
    return this.build()
        .stream()
        .map(TextUtil::parse)
        .collect(Collectors.toList());
  }

  public void sendLegacy(CommandSender commandSender) {
    send(Collections.singletonList(commandSender), true);
  }

  public void send(CommandSender commandSender) {
    send(Collections.singletonList(commandSender), false);
  }

  public void send(CommandSender commandSender, boolean legacy) {
    send(Collections.singletonList(commandSender), legacy);
  }

  public void send(Collection<CommandSender> receivers, boolean legacy) {
    List<String> messages = build();
    if (receivers.isEmpty() || messages.isEmpty()) {
      return;
    }

    for (CommandSender commandSender : receivers) {

      for (String message : messages) {
        if (legacy) {
          commandSender.sendMessage(TextUtil.legacyColor(message));
        } else {
          commandSender.sendMessage(TextUtil.parse(message));
        }

      }

    }
  }

  public String firstLine() {
    return build().get(0);
  }

}
