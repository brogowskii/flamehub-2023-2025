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
    this.packetResponseCache = new PacketResponseCache();
  }

  @Override
  public <T extends Packet> void publish(String channel, T message) {
    RTopic topic = redissonClient.getTopic(channel);
    topic.publish(message);
  }

  @Override
  public <T extends Packet> void publishMany(String channel, T[] messages) {
    RTopic topic = redissonClient.getTopic(channel);
    for (T message : messages) {
      topic.publish(message);
    }
  }

  @Override
  public void subscribe(String channel, Object listener) {

    Map<String, Method> methodByType = new ConcurrentHashMap<>();
    Arrays.stream(listener.getClass().getDeclaredMethods())
        .filter(method -> method.getParameters().length == 1 && method.isAnnotationPresent(PacketHandler.class))
        .forEach(method -> {
          method.setAccessible(true);
          methodByType.put(method.getParameters()[0].getType().getName(), method);
        });

    RTopic topic = redissonClient.getTopic(channel);
    topic.addListener(Packet.class, new PacketListener(methodByType, listener));
  }

  @Override
  public void subscribe(String[] channels, Object listener) {
    for (String channel : channels) {
      subscribe(channel, listener);
    }
  }

  @Override
  public void subscribeMany(String channel, Object[] listeners) {
    for (Object listener : listeners) {
      subscribe(channel, listener);
    }
  }

  @Override
  public void subscribeMany(String[] channels, Object[] listeners) {
    for (String channel : channels) {
      subscribeMany(channel, listeners);
    }
  }

  @Override
  public void subscribeCallbacks(String channel) {
    RTopic topic = redissonClient.getTopic(channel);
    topic.addListener(Packet.class, new PacketResponseListener(packetResponseCache));
  }

  @Override
  public <T extends PacketResponse> CompletableFuture<T> publishFuture(String channel, PacketRequest request) {
    CompletableFuture<T> completableFuture = new CompletableFuture<>();
    packetResponseCache.add(request.getUniqueId(), completableFuture);
    publish(channel, request);
    return completableFuture;
  }
}
