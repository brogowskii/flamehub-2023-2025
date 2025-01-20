package io.github.flamehub.commons.messenger.packet;


import io.github.flamehub.commons.messenger.codec.PacketGsonCodec;
import io.lettuce.core.pubsub.RedisPubSubListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public final class PacketListener implements RedisPubSubListener<String, String> {

  private final Map<String, Method> methodsByName;

  private final Object messageHandler;
  private final String subscribedChannel;

  public PacketListener(Map<String, Method> methodsByName, Object messageHandler,
      String subscribedChannel) {
    this.methodsByName = methodsByName;
    this.messageHandler = messageHandler;
    this.subscribedChannel = subscribedChannel;
  }

  @Override
  public void message(String channel, String json) {
    if (!channel.equals(subscribedChannel)) {
      return;
    }

    Packet packet = PacketGsonCodec.deserialize(json);
    if (packet == null) {
      return;
    }

    Method method = methodsByName.get(packet.getClass().getName());
    if (method == null) {
      return;
    }

    try {
      method.invoke(messageHandler, packet);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void message(String s, String k1, String json) {

  }

  @Override
  public void subscribed(String s, long l) {

  }

  @Override
  public void psubscribed(String s, long l) {

  }

  @Override
  public void unsubscribed(String s, long l) {

  }

  @Override
  public void punsubscribed(String s, long l) {

  }
}
