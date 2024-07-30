package io.github.flamehub.essentials.teleport;

import io.github.flamehub.commons.messenger.packet.PacketRequest;
import java.util.UUID;

public final class TeleportPacketRequest extends PacketRequest {

  private final UUID requesterUUID;
  private final String targetName;

  public TeleportPacketRequest(UUID requesterUUID, String targetName) {
    this.requesterUUID = requesterUUID;
    this.targetName = targetName;
  }

  public UUID getRequesterUUID() {
    return requesterUUID;
  }

  public String getTargetName() {
    return targetName;
  }
}
