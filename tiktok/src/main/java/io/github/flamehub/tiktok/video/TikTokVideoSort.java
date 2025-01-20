package io.github.flamehub.tiktok.video;

public enum TikTokVideoSort {
  NONE("Brak"),
  NEWEST("Najnowsze"),
  OLDEST("Najstarsze"),
  VIEWS("Najwięcej wyświetleń"),
  LIKES("Najwięcej polubień"),
  COMMENTS("Najwięcej komentarzy");

  private final String name;

  TikTokVideoSort(String name) {
    this.name = name;
  }

  public TikTokVideoSort next() {
    int nextIndex = (ordinal() + 1) % values().length;
    return values()[nextIndex];
  }

  public String getName() {
    return name;
  }
}
