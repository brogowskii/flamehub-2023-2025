package io.github.flamehub.commons.network.message;

import io.github.flamehub.commons.messenger.RedisMessenger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class NetworkMessageService {

  private final RedisMessenger redisMessenger;
  private final String channel;

  public NetworkMessageService(final RedisMessenger redisMessenger, final String channel) {
    this.redisMessenger = redisMessenger;
    this.channel = channel;
  }

  public void send(
      final String message,
      final NetworkMessageFilter filter,
      final NetworkMessageType type) {

    final NetworkMessage networkMessage = new NetworkMessage(
        Collections.singletonList(message),
        filter,
        type);

    redisMessenger.publish(channel, networkMessage);
  }

  public void send(
      final List<String> messages,
      final NetworkMessageFilter filter,
      final NetworkMessageType type) {
    final NetworkMessage networkMessage = new NetworkMessage(messages, filter, type);
    redisMessenger.publish(channel, networkMessage);
  }

  public void send(final String message, final NetworkMessageType type) {
    send(message, NetworkMessageFilter.builder().build(), type);
  }

  public void send(final List<String> messages, final NetworkMessageType type) {
    send(messages, NetworkMessageFilter.builder().build(), type);
  }

  public CompletableFuture<Void> sendAsync(
      final String message,
      final NetworkMessageFilter filter,
      final NetworkMessageType type) {
    return CompletableFuture.runAsync(() -> send(message, filter, type));
  }

  public CompletableFuture<Void> sendAsync(
      final List<String> messages,
      final NetworkMessageFilter filter,
      final NetworkMessageType type) {
    return CompletableFuture.runAsync(() -> send(messages, filter, type));
  }

  public CompletableFuture<Void> sendAsync(final String message, final NetworkMessageType type) {
    return CompletableFuture.runAsync(() -> send(message, type));
  }

  public CompletableFuture<Void> sendAsync(final List<String> messages,
      final NetworkMessageType type) {
    return CompletableFuture.runAsync(() -> send(messages, type));
  }
}
