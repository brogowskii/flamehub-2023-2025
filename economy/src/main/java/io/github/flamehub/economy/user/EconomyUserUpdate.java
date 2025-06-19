package io.github.flamehub.economy.user;

import io.github.flamehub.commons.messenger.packet.Packet;
import java.util.UUID;

public final class EconomyUserUpdate implements Packet {

  private UUID uniqueId;
  private double money;
  private EconomyUserUpdateType type;

  public EconomyUserUpdate() {
  }

  public EconomyUserUpdate(final UUID uniqueId, final double money, EconomyUserUpdateType type) {
    this.uniqueId = uniqueId;
    this.money = money;
    this.type = type;
  }

  public EconomyUserUpdateType getType() {
    return type;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public double getMoney() {
    return money;
  }
}
