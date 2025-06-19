package io.github.flamehub.proxy.core.player;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class PlayerKickPacket implements Packet {

  private String playerName;
  private String reason;

  public PlayerKickPacket() {
  }

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
