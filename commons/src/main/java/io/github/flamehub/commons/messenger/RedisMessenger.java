package io.github.flamehub.commons.messenger;

import io.github.flamehub.commons.messenger.packet.Packet;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.commons.messenger.packet.PacketListener;
import io.github.flamehub.commons.messenger.packet.PacketRequest;
import io.github.flamehub.commons.messenger.packet.PacketResponse;
import io.github.flamehub.commons.messenger.packet.PacketResponseCache;
import io.github.flamehub.commons.messenger.packet.PacketResponseListener;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

public final class RedisMessenger implements Messenger {

  private final RedissonClient redissonClient;
  private final PacketResponseCache packetResponseCache;

  public RedisMessenger(final RedissonClient redissonClient) {
    this.redissonClient = redissonClient;
    packetResponseCache = new PacketResponseCache();
  }

  @Override
  public <T extends Packet> void publish(final String channel, final T message) {
    final RTopic topic = redissonClient.getTopic(channel);
    topic.publish(message);
  }

  @Override
  public <T extends Packet> void publishMany(final String channel, final T[] messages) {
    final RTopic topic = redissonClient.getTopic(channel);
    for (final T message : messages) {
      topic.publish(message);
    }
  }

  @Override
  public void subscribe(final String channel, final Object listener) {

    final Map<String, Method> methodByType = new ConcurrentHashMap<>();
    Arrays.stream(listener.getClass().getDeclaredMethods())
        .filter(method -> method.getParameters().length == 1 && method.isAnnotationPresent(
            PacketHandler.class))
        .forEach(method -> {
          method.setAccessible(true);
          methodByType.put(method.getParameters()[0].getType().getName(), method);
        });

    final RTopic topic = redissonClient.getTopic(channel);
    topic.addListener(Packet.class, new PacketListener(methodByType, listener));
  }

  @Override
  public void subscribe(final String[] channels, final Object listener) {
    for (final String channel : channels) {
      subscribe(channel, listener);
    }
  }

  @Override
  public void subscribeMany(final String channel, final Object[] listeners) {
    for (final Object listener : listeners) {
      subscribe(channel, listener);
    }
  }

  @Override
  public void subscribeMany(final String[] channels, final Object[] listeners) {
    for (final String channel : channels) {
      subscribeMany(channel, listeners);
    }
  }

  @Override
  public void subscribeCallbacks(final String channel) {
    final RTopic topic = redissonClient.getTopic(channel);
    topic.addListener(Packet.class, new PacketResponseListener(packetResponseCache));
  }

  @Override
  public <T extends PacketResponse> CompletableFuture<T> publishFuture(final String channel,
      final PacketRequest request) {
    final CompletableFuture<T> completableFuture = new CompletableFuture<>();
    packetResponseCache.add(request.getUniqueId(), completableFuture);
    publish(channel, request);
    return completableFuture;
  }
}
