package io.github.flamehub.proxy.core.player;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class PlayerKickPacket implements Packet {

  private final String playerName;
  private final String reason;

  public PlayerKickPacket(final String playerName, final String reason) {
    this.playerName = playerName;
    this.reason = reason;
  }

  public String getPlayerName() {
    return playerName;
  }

  public String getReason() {
    return reason;
  }
}
