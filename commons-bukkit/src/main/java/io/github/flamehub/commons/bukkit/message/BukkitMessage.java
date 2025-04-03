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

  public static BukkitMessage from(String message) {
    return new BukkitMessage().add(message);
  }

  public static BukkitMessage from(List<String> messages) {
    return new BukkitMessage().add(messages);
  }

  public static BukkitMessage from(String... messages) {
    return new BukkitMessage().add(messages);
  }

  @Override
  public BukkitMessage add(String message) {
    return (BukkitMessage) super.add(message);
  }

  @Override
  public BukkitMessage add(List<String> messages) {
    return (BukkitMessage) super.add(messages);
  }

  @Override
  public BukkitMessage add(String... messages) {
    return (BukkitMessage) super.add(messages);
  }

  @Override
  public BukkitMessage with(String from, Object to) {
    return (BukkitMessage) super.with(from, to);
  }

  public void deliver(CommandSender commandSender) {
    apply().forEach(s -> commandSender.sendMessage(TextUtil.parse(s)));
  }

  public void deliver(Collection<CommandSender> commandSenders) {
    for (final CommandSender viewer : commandSenders) {
      deliver(viewer);
    }
  }

  @JsonIgnore
  public List<Component> applyAsComponent() {
    List<Component> deserialized = new ArrayList<>();
    for (final String s : apply()) {
      deserialized.add(TextUtil.parse(s));
    }
    return deserialized;
  }

  @JsonIgnore
  public Component applyFirstAsComponent() {
    return TextUtil.parse(apply().getFirst());
  }

  public CompletableFuture<Void> deliverAsync(CommandSender sender) {
    return runAsync(() -> deliver(sender));
  }

  public CompletableFuture<Void> deliverAsync(Collection<CommandSender> commandSenders) {
    return runAsync(() -> deliver(commandSenders));
  }

}
