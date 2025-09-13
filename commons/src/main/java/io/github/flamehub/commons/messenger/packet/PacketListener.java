package io.github.flamehub.commons.messenger.packet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.redisson.api.listener.MessageListener;

public final class PacketListener implements MessageListener<Packet> {

  private final Map<String, Method> methodsByName;
  private final Object messageHandler;

  public PacketListener(final Map<String, Method> methodsByName, final Object messageHandler) {
    this.methodsByName = methodsByName;
    this.messageHandler = messageHandler;
  }

  @Override
  public void onMessage(final CharSequence channel, final Packet packet) {
    if (packet == null) {
      return;
    }
    final Method method = methodsByName.get(packet.getClass().getName());
    if (method == null) {
      return;
    }
    try {
      method.invoke(messageHandler, packet);
    } catch (final IllegalAccessException e) {
      System.err.println(
          "IllegalAccessException while invoking method for packet: " + packet.getClass()
              .getName());
      e.printStackTrace();
    } catch (final InvocationTargetException e) {
      System.err.println(
          "InvocationTargetException while invoking method for packet: " + packet.getClass()
              .getName());
      e.printStackTrace();
      if (e.getCause() != null) {
        System.err.println("Cause of InvocationTargetException: ");
        e.getCause().printStackTrace();
      }
    } catch (final Exception e) {
      System.err.println(
          "Unexpected exception while invoking method for packet: " + packet.getClass().getName());
      e.printStackTrace();
    }
  }
}