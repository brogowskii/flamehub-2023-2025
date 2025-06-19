package io.github.flamehub.commons.punishment;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class PunishmentKickPacket implements Packet {

  private String player;
  private String reason;

  public PunishmentKickPacket() {
  }

  public PunishmentKickPacket(final String player, final String reason) {
    this.player = player;
    this.reason = reason;
  }

  public String getPlayer() {
    return player;
  }

  public String getReason() {
    return reason;
  }
}
