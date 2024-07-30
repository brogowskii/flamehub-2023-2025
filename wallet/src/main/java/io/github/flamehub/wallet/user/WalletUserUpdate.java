package io.github.flamehub.wallet.user;

import io.github.flamehub.commons.messenger.packet.Packet;
import io.github.flamehub.wallet.api.WalletUserMoneyChangeType;
import java.util.UUID;

public final class WalletUserUpdate implements Packet {

  private final UUID uniqueId;
  private final WalletUserMoneyChangeType type;
  private final double amount;

  public WalletUserUpdate(UUID uniqueId, WalletUserMoneyChangeType type, double amount) {
    this.uniqueId = uniqueId;
    this.type = type;
    this.amount = amount;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public double getAmount() {
    return amount;
  }

  public WalletUserMoneyChangeType getType() {
    return type;
  }
}
