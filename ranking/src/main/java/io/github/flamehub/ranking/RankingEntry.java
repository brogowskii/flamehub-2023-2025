package io.github.flamehub.ranking;

public final class RankingEntry {

  private final String name;
  private final Object value;

  public RankingEntry(String name, Object value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    return value;
  }
}