package io.github.flamehub.commons.network.message;

import io.github.flamehub.commons.messenger.packet.Packet;
import java.util.List;

public final class NetworkMessage implements Packet {

  private List<String> messages;
  private NetworkMessageFilter filter;
  private NetworkMessageType type;

  public NetworkMessage() {
  }

  public NetworkMessage(
      final List<String> messages,
      final NetworkMessageFilter filter,
      final NetworkMessageType type
  ) {
    this.messages = messages;
    this.filter = filter;
    this.type = type;
  }

  public List<String> getMessages() {
    return messages;
  }

  public NetworkMessageFilter getFilter() {
    return filter;
  }

  public NetworkMessageType getType() {
    return type;
  }
}
