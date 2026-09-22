package io.github.flamehub.contest.user;

import io.github.flamehub.commons.messenger.packet.Packet;
import java.util.UUID;

public final class ContestUserUpdate implements Packet {

  private UUID uniqueId;
  private double value;
  private ContestUserUpdateType type;

  public ContestUserUpdate() {
  }

  public ContestUserUpdate(final UUID uniqueId, final double value, final ContestUserUpdateType type) {
    this.uniqueId = uniqueId;
    this.value = value;
    this.type = type;
  }

  public ContestUserUpdateType getType() {
    return type;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public double getValue() {
    return value;
  }
}
