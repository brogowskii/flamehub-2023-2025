package io.github.flamehub.timeplayed.shop;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.HashMap;
import java.util.Map;

@FlameConfigProperties(name = "timePlayedShop.json")
@EnableRemote(collection = "configs")
public final class TimePlayedShopConfig extends FlameConfig {

  private final Map<Integer, TimePlayedShopItem> itemsBySlot = new HashMap<>();

  public TimePlayedShopConfig() {
  }

  public Map<Integer, TimePlayedShopItem> getItemsBySlot() {
    return itemsBySlot;
  }
}
