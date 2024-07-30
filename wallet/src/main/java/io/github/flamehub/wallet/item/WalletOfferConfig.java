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

  private List<WalletOffer> walletOffers = Arrays.asList(
      new WalletOffer(
          "&7Ranga &eVIP",
          Arrays.asList(
              "",
              " &7Cena kupna zaczyna się od: &e{PRICE} vPLN",
              ""
          ),
          Material.IRON_HELMET,
          11,
          Arrays.asList(
              new WalletOfferVariant("&7Ranga &eVIP&7, okres trwania:", Arrays.asList(""), 5, 1,
                  Arrays.asList(""), ""))
      )
  );

  public WalletOfferConfig() {
  }

  public List<WalletOffer> getWalletOffers() {
    return walletOffers;
  }
}
