package io.github.flamehub.wallet.item;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;

@FlameConfigProperties(name = "walletOffer.json")
@EnableRemote(collection = "configs")
public final class WalletOfferConfig extends FlameConfig {

  private final List<WalletOffer> walletOffers = List.of(
      new WalletOffer(
          "&7Ranga &eVIP",
          Arrays.asList(
              "",
              " &7Cena kupna zaczyna się od: &e{PRICE} vPLN",
              ""
          ),
          Material.IRON_HELMET,
          0,
          11,
          List.of(
              new WalletOfferVariant("&7Ranga &eVIP&7, okres trwania:", List.of(""), 5, 1,
                  List.of(""), ""))
      )
  );

  public WalletOfferConfig() {
  }

  public List<WalletOffer> getWalletOffers() {
    return walletOffers;
  }
}
