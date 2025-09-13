package io.github.flamehub.ranking;

import java.util.ArrayList;
import java.util.List;

public final class RankingEntry {

  private final String name;
  private final List<Object> value = new ArrayList<>();

  public RankingEntry(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public List<Object> getValue() {
    return value;
  }
}