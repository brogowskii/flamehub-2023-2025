package io.github.flamehub.commons.messenger.packet;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.UUID;

public class PacketResponse implements Packet {

  private UUID uniqueId;

  public PacketResponse() {
  }

  public PacketResponse(UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

}
