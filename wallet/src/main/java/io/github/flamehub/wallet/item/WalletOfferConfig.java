package io.github.flamehub.wallet.item;

import eu.okaeri.configs.OkaeriConfig;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class WalletOfferConfig extends OkaeriConfig {

    private Set<WalletOffer> walletOffers = Set.of(
            new WalletOffer(
                    "&7Ranga &eVIP",
                    Arrays.asList(
                            "",
                            " &7Cena kupna zaczyna się od: &e{PRICE} vPLN",
                            ""
                    ),
                    Material.IRON_HELMET,
                    11,
                    List.of(new WalletOfferVariant("&7Ranga &eVIP&7, okres trwania:", List.of(""), 5, 1, List.of(""), ""))
            )
    );

    public Set<WalletOffer> getWalletItems() {
        return walletOffers;
    }
}
