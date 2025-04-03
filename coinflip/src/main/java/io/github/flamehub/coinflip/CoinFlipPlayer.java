package io.github.flamehub.coinflip;

import java.util.UUID;

public final class CoinFlipPlayer {

  private final String name;
  private final UUID uniqueId;

  public CoinFlipPlayer(final String name, final UUID uniqueId) {
    this.name = name;
    this.uniqueId = uniqueId;
  }

  public String getName() {
    return name;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

}
