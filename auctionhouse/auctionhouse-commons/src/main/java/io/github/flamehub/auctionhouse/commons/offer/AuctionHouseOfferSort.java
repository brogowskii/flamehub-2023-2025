package io.github.flamehub.auctionhouse.commons.offer;

public enum AuctionHouseOfferSort {

  NONE("Brak"),
  ASCENDING_PRICE("Najniższej ceny"),
  DESCENDING_PRICE("Najwyższej ceny"),
  NEWEST("Najnowsze"),
  OLDEST("Najstarsze");

  private final String name;

  AuctionHouseOfferSort(String name) {
    this.name = name;
  }

  public AuctionHouseOfferSort next() {
    int nextIndex = (this.ordinal() + 1) % values().length;
    return values()[nextIndex];
  }

  public String getName() {
    return name;
  }
}
