package io.github.flamehub.wallet.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Material;

public final class WalletOffer implements Serializable {

  private String offer;
  private List<String> lore;
  private Material icon;
  private int customModelData;

  private int slot;
  private List<WalletOfferVariant> variants;

  public WalletOffer() {
  }

  public WalletOffer(String offer, List<String> lore, Material icon, final int customModelData, int slot,
      List<WalletOfferVariant> variants) {
    this.offer = offer;
    this.lore = lore;
    this.icon = icon;
    this.customModelData = customModelData;
    this.slot = slot;
    this.variants = variants;
  }

  @JsonIgnore
  public WalletOfferVariant lowestPriceVariant() {
    return this.variants.stream()
        .min(Comparator.comparingDouble(WalletOfferVariant::getCost))
        .orElse(null);
  }

  public String getOffer() {
    return offer;
  }

  public List<String> getLore() {
    return lore;
  }

  public Material getIcon() {
    return icon;
  }

  public int getSlot() {
    return slot;
  }

  public List<WalletOfferVariant> getVariants() {
    return variants;
  }

  public int getCustomModelData() {
    return customModelData;
  }
}