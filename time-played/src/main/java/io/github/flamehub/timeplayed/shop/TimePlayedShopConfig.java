package io.github.flamehub.timeplayed.shop;

import io.github.flamehub.commons.legacy.config.MongoConfig;

import java.util.HashMap;
import java.util.Map;

public final class  TimePlayedShopConfig extends MongoConfig {

    private Map<Integer, TimePlayedShopItem> itemsBySlot = new HashMap<>();

    public TimePlayedShopConfig() {
    }

    public TimePlayedShopConfig(String id) {
        super(id);
    }

    public Map<Integer, TimePlayedShopItem> getItemsBySlot() {
        return itemsBySlot;
    }
}
