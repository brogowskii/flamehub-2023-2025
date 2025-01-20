package io.github.flamehub.commons.messenger;

import io.github.flamehub.commons.messenger.codec.PacketGsonCodec;
import io.github.flamehub.commons.messenger.packet.Packet;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.commons.messenger.packet.PacketListener;
import io.github.flamehub.commons.messenger.packet.PacketRequest;
import io.github.flamehub.commons.messenger.packet.PacketResponse;
import io.github.flamehub.commons.messenger.packet.PacketResponseCache;
import io.github.flamehub.commons.messenger.packet.PacketResponseListener;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class RedisMessenger implements Messenger {

//    private final static RedisCodec<String, Packet> CODEC = new PacketKryoCodec();

  private final StatefulRedisPubSubConnection<String, String> pubSubConnection;
  private final StatefulRedisConnection<String, String> connection;


  private final Set<String> subscribedChannels;
  private final PacketResponseCache packetResponseCache;

  public RedisMessenger(RedisClient client) {
    this.connection = client.connect();
    this.pubSubConnection = client.connectPubSub();
    this.subscribedChannels = new HashSet<>();
    this.packetResponseCache = new PacketResponseCache();
  }

  @Override
  public <T extends Packet> void publish(String channel, T message) {
    connection.sync().publish(channel, PacketGsonCodec.serialize(message));
  }

  @Override
  public <T extends Packet> void publishMany(String channel, T[] messages) {
    RedisCommands<String, String> sync = connection.sync();
    sync.multi();
    for (T message : messages) {
      sync.publish(channel, PacketGsonCodec.serialize(message));
    }

    sync.exec();

  }

  @Override
  public void subscribe(String channel, Object listener) {

    if (!subscribedChannels.contains(channel)) {
      pubSubConnection.sync().subscribe(channel);
      subscribedChannels.add(channel);
    }

    Map<String, Method> methodByType = new ConcurrentHashMap<>();
    Arrays.stream(listener.getClass().getDeclaredMethods())
        .filter(method -> method.getParameters().length == 1 && method.isAnnotationPresent(
            PacketHandler.class))
        .forEach(
            method -> methodByType.put(method.getParameters()[0].getType().getName(), method));

    PacketListener packetListener = new PacketListener(methodByType, listener, channel);
    pubSubConnection.addListener(packetListener);

  }

  public void subscribe(String channel, Object listener, Class<?> listenerClass) {

    if (!subscribedChannels.contains(channel)) {
      pubSubConnection.sync().subscribe(channel);
      subscribedChannels.add(channel);
    }

    Map<String, Method> stringMethodMap = new ConcurrentHashMap<>();
    Arrays.stream(listenerClass.getDeclaredMethods())
        .peek(method -> method.setAccessible(true))
        .filter(method -> method.getParameters().length == 1 && method.isAnnotationPresent(
            PacketHandler.class))
        .forEach(
            method -> stringMethodMap.put(method.getParameters()[0].getType().getName(), method));

    PacketListener packetListener = new PacketListener(stringMethodMap, listener, channel);
    pubSubConnection.addListener(packetListener);

  }

  @Override
  public void subscribe(String[] channels, Object listener) {
    for (String channel : channels) {
      subscribe(channel, listener);
    }
  }

  @Override
  public void subscribeMany(String channel, Object[] listener) {
    for (Object o : listener) {
      subscribe(channel, o);
    }
  }

  @Override
  public void subscribeMany(String[] channels, Object[] listener) {
    for (String channel : channels) {
      subscribeMany(channel, listener);
    }
  }

  @Override
  public void subscribeCallbacks(String channel) {

    if (!subscribedChannels.contains(channel)) {
      pubSubConnection.sync().subscribe(channel);
      subscribedChannels.add(channel);
    }

    PacketResponseListener packetResponseListener = new PacketResponseListener(
        packetResponseCache, channel);
    pubSubConnection.addListener(packetResponseListener);

  }

  @Override
  public <T extends PacketResponse> CompletableFuture<T> publishFuture(String channel,
      PacketRequest request) {

    CompletableFuture<T> completableFuture = new CompletableFuture<>();
    packetResponseCache.add(request.getUniqueId(), completableFuture);
    publish(channel, request);
    return completableFuture;

  }
}
