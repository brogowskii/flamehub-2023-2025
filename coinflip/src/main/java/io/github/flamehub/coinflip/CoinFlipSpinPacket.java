package io.github.flamehub.coinflip;

import io.github.flamehub.commons.messenger.packet.Packet;
import java.util.UUID;

public final class CoinFlipSpinPacket implements Packet {

  private UUID playerUniqueId;
  private byte[] winnerHead;
  private byte[] loserHead;

  public CoinFlipSpinPacket(
      final UUID playerUniqueId,
      final byte[] winnerHead,
      final byte[] loserHead
  ) {
    this.playerUniqueId = playerUniqueId;
    this.winnerHead = winnerHead;
    this.loserHead = loserHead;
  }

  public CoinFlipSpinPacket() {
  }

  public UUID getPlayerUniqueId() {
    return playerUniqueId;
  }

  public byte[] getWinnerHead() {
    return winnerHead;
  }

  public byte[] getLoserHead() {
    return loserHead;
  }
}
