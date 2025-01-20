package io.github.flamehub.marketplace;

import dev.morphia.annotations.Entity;
import java.util.UUID;

@Entity
public final class MarketSeller {

  private UUID uniqueId;
  private String name;

  public MarketSeller() {

  }

  public MarketSeller(final UUID uniqueId, final String name) {
    this.uniqueId = uniqueId;
    this.name = name;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public String getName() {
    return name;
  }
}
