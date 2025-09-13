package io.github.flamehub.commons.bukkit.message;

import static java.util.concurrent.CompletableFuture.runAsync;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.message.Message;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public final class BukkitMessage extends Message {

  public static BukkitMessage from(final String message) {
    return new BukkitMessage().add(message);
  }

  public static BukkitMessage from(final List<String> messages) {
    return new BukkitMessage().add(messages);
  }

  public static BukkitMessage from(final String... messages) {
    return new BukkitMessage().add(messages);
  }

  @Override
  public BukkitMessage add(final String message) {
    return (BukkitMessage) super.add(message);
  }

  @Override
  public BukkitMessage add(final List<String> messages) {
    return (BukkitMessage) super.add(messages);
  }

  @Override
  public BukkitMessage add(final String... messages) {
    return (BukkitMessage) super.add(messages);
  }

  @Override
  public BukkitMessage with(final String from, final Object to) {
    return (BukkitMessage) super.with(from, to);
  }

  public void deliver(final CommandSender commandSender) {
    apply().forEach(s -> commandSender.sendMessage(TextUtil.parse(s)));
  }

  public void deliver(final Collection<CommandSender> commandSenders) {
    for (final CommandSender viewer : commandSenders) {
      deliver(viewer);
    }
  }

  @JsonIgnore
  public List<Component> applyAsComponent() {
    final List<Component> deserialized = new ArrayList<>();
    for (final String s : apply()) {
      deserialized.add(TextUtil.parse(s));
    }
    return deserialized;
  }

  @JsonIgnore
  public Component applyFirstAsComponent() {
    return TextUtil.parse(apply().getFirst());
  }

  public CompletableFuture<Void> deliverAsync(final CommandSender sender) {
    return runAsync(() -> deliver(sender));
  }

  public CompletableFuture<Void> deliverAsync(final Collection<CommandSender> commandSenders) {
    return runAsync(() -> deliver(commandSenders));
  }

}
