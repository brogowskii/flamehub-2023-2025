package io.github.flamehub.marketplace.offer;

public enum MarketOfferFilter {

  ALL("Wyświetlaj wszystkie przedmioty"),
  ENOUGH_MONEY("Wyświetlaj tylko te, na które Cię stać");

  private final String name;

  MarketOfferFilter(final String name) {
    this.name = name;
  }

  public MarketOfferFilter next() {
    int nextIndex = (ordinal() + 1) % values().length;
    return values()[nextIndex];
  }

  public String getName() {
    return name;
  }
}
