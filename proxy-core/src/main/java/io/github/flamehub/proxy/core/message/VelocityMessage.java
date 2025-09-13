package io.github.flamehub.proxy.core.message;

import static java.util.concurrent.CompletableFuture.runAsync;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.velocitypowered.api.command.CommandSource;
import io.github.flamehub.commons.message.Message;
import io.github.flamehub.proxy.core.util.TextUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;

public final class VelocityMessage extends Message {

  public static VelocityMessage from(final String message) {
    return new VelocityMessage().add(message);
  }

  public static VelocityMessage from(final List<String> messages) {
    return new VelocityMessage().add(messages);
  }

  public static VelocityMessage from(final String... messages) {
    return new VelocityMessage().add(messages);
  }

  @Override
  public VelocityMessage add(final String message) {
    return (VelocityMessage) super.add(message);
  }

  @Override
  public VelocityMessage add(final List<String> messages) {
    return (VelocityMessage) super.add(messages);
  }

  @Override
  public VelocityMessage add(final String... messages) {
    return (VelocityMessage) super.add(messages);
  }

  @Override
  public VelocityMessage with(final String from, final Object to) {
    return (VelocityMessage) super.with(from, to);
  }

  public void deliver(final CommandSource commandSource) {
    apply().forEach(s -> commandSource.sendMessage(TextUtil.parse(s)));
  }

  public void deliver(final Collection<CommandSource> commandSources) {
    for (final CommandSource viewer : commandSources) {
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

  public CompletableFuture<Void> deliverAsync(final CommandSource commandSource) {
    return runAsync(() -> deliver(commandSource));
  }

  public CompletableFuture<Void> deliverAsync(final Collection<CommandSource> commandSources) {
    return runAsync(() -> deliver(commandSources));
  }


}
