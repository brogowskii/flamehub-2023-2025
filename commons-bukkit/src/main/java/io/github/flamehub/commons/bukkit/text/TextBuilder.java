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

  public TextBuilder text(final String message) {
    text.add(message);
    return this;
  }

  public TextBuilder text(final List<String> message) {
    text.addAll(message);
    return this;
  }

  public TextBuilder text(final String... message) {
    text.addAll(List.of(message));
    return this;
  }

  public TextBuilder placeholder(final String from, final Object to) {
    if (to == null) {
      placeholders.put(from, from + "=null");
      return this;
    }
    placeholders.put(from, to);
    return this;
  }

  public List<String> build() {
    if (!placeholders.isEmpty()) {
      final List<String> replacedMessages = new ArrayList<>();

      for (final String message : text) {
        String messageToReplace = message;

        for (final Map.Entry<String, Object> entry : placeholders.entrySet()) {
          final Object value = entry.getValue();
          final String key = entry.getKey();
          messageToReplace = messageToReplace.replace(key, value.toString());
        }

        replacedMessages.add(messageToReplace);
      }

      return replacedMessages;
    }

    return text;
  }

  public List<Component> buildAsComponents() {
    return build()
        .stream()
        .map(TextUtil::parse)
        .collect(Collectors.toList());
  }

  public void sendLegacy(final CommandSender commandSender) {
    send(Collections.singletonList(commandSender), true);
  }

  public void send(final CommandSender commandSender) {
    send(Collections.singletonList(commandSender), false);
  }

  public void send(final CommandSender commandSender, final boolean legacy) {
    send(Collections.singletonList(commandSender), legacy);
  }

  public void send(final Collection<CommandSender> receivers, final boolean legacy) {
    final List<String> messages = build();
    if (receivers.isEmpty() || messages.isEmpty()) {
      return;
    }

    for (final CommandSender commandSender : receivers) {

      for (final String message : messages) {
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
