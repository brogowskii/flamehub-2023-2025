package io.github.flamehub.marketplace.offer;

public enum MarketOfferSort {

  NONE("Brak"),
  LEVEL("Poziomu"),
  ASCENDING_PRICE("Najniższej ceny"),
  DESCENDING_PRICE("Najwyższej ceny"),
  NEWEST("Najnowsze"),
  OLDEST("Najstarsze");

  private final String name;

  MarketOfferSort(final String name) {
    this.name = name;
  }

  public MarketOfferSort next() {
    final int nextIndex = (ordinal() + 1) % values().length;
    return values()[nextIndex];
  }

  public String getName() {
    return name;
  }
}
