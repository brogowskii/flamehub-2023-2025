package io.github.flamehub.reward.api;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class RewardReceivedPacket implements Packet {

  private final String playerName;

  public RewardReceivedPacket(String playerName) {
    this.playerName = playerName;
  }

  public String getPlayerName() {
    return playerName;
  }
}
