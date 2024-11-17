package io.github.flamehub.tiktok.shop;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;

@FlameConfigProperties(name = "tiktokShop.json")
@EnableRemote(collection = "configs")
public final class TikTokShopConfig extends FlameConfig {

  private Set<TikTokShopItem> shopItems = Set.of(new TikTokShopItem(100, new ArrayList<>(),
      Material.PAPER, "MEDIA", List.of("cwel"), 11));

  public TikTokShopConfig() {
  }

  public TikTokShopConfig(final Set<TikTokShopItem> shopItems) {
    this.shopItems = shopItems;
  }

  public Set<TikTokShopItem> getShopItems() {
    return shopItems;
  }
}
