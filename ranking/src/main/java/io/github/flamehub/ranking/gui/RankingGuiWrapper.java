package io.github.flamehub.ranking.gui;

import java.io.Serializable;

public final class RankingGuiWrapper implements Serializable {

  private String id;
  private String guiName;

  public RankingGuiWrapper() {
  }

  public RankingGuiWrapper(String id, String guiName) {
    this.id = id;
    this.guiName = guiName;
  }

  public String getId() {
    return id;
  }

  public String getGuiName() {
    return guiName;
  }
}
