package io.github.flamehub.marketplace.category;

import java.io.Serializable;
import java.util.List;

public final class MarketCategory implements Serializable {

  private String id;
  private String friendlyName;
  private List<String> materials;

  public MarketCategory() {

  }

  public MarketCategory(String id, String friendlyName,
      List<String> materials) {
    this.id = id;
    this.friendlyName = friendlyName;
    this.materials = materials;
  }

  public String getId() {
    return id;
  }

  public String getFriendlyName() {
    return friendlyName;
  }

  public List<String> getMaterials() {
    return materials;
  }
}
