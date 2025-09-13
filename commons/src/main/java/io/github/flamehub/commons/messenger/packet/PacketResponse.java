package io.github.flamehub.commons.messenger.packet;

import java.util.UUID;

public class PacketResponse implements Packet {

  private UUID uniqueId;

  public PacketResponse() {
  }

  public PacketResponse(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

}
