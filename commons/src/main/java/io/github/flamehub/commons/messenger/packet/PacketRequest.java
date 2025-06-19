package io.github.flamehub.commons.messenger.packet;

import java.util.UUID;

public class PacketRequest implements Packet {

  private UUID uniqueId = UUID.randomUUID();

  public PacketRequest() {
  }

  public UUID getUniqueId() {
    return uniqueId;
  }
}
