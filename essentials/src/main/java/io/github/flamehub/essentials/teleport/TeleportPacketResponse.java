package io.github.flamehub.essentials.teleport;

import io.github.flamehub.commons.messenger.packet.PacketResponse;
import java.util.UUID;

public final class TeleportPacketResponse extends PacketResponse {

  public TeleportPacketResponse(UUID uniqueId) {
    super(uniqueId);
  }
}
