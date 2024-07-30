package io.github.flamehub.commons.bukkit.censure;

import java.io.Serializable;

public final class CensureReplacement implements Serializable {

  private String from;
  private String to;

  public CensureReplacement(String from, String to) {
    this.from = from;
    this.to = to;
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }
}
