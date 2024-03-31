package io.github.flamehub.wallet.item;

import org.bukkit.Material;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public final class WalletOffer implements Serializable {

    private final String offer;
    private final List<String> lore;
    private final Material icon;
    private final int slot;
    private final List<WalletOfferVariant> variants;

    public WalletOffer(String offer, List<String> lore, Material icon, int slot, List<WalletOfferVariant> variants) {
        this.offer = offer;
        this.lore = lore;
        this.icon = icon;
        this.slot = slot;
        this.variants = variants;
    }

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

}