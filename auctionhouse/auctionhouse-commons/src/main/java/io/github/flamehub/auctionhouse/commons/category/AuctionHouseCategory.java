package io.github.flamehub.auctionhouse.commons.category;

import java.io.Serializable;
import java.util.List;

public final class AuctionHouseCategory implements Serializable {

    private String id;
    private String friendlyName;
    private AuctionHouseCategoryIcon icon;
    private List<String> materials;

    public AuctionHouseCategory() {

    }

    public AuctionHouseCategory(String id, String friendlyName, AuctionHouseCategoryIcon icon, List<String> materials) {
        this.id = id;
        this.friendlyName = friendlyName;
        this.icon = icon;
        this.materials = materials;
    }

    public String getId() {
        return id;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public AuctionHouseCategoryIcon getIcon() {
        return icon;
    }

    public List<String> getMaterials() {
        return materials;
    }
}
