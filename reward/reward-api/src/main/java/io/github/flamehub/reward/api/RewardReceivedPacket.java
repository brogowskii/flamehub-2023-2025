package io.github.flamehub.reward.api;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class RewardReceivedPacket implements Packet {

  private String playerName;

  public RewardReceivedPacket() {
  }

  public RewardReceivedPacket(final String playerName) {
    this.playerName = playerName;
  }

  public String getPlayerName() {
    return playerName;
  }
}
