package io.github.flamehub.kits;

import io.github.flamehub.commons.config.MongoConfig;
import io.github.flamehub.kits.kit.Kit;

import java.util.ArrayList;
import java.util.List;

public final class KitsConfig extends MongoConfig {

    private String kitUsersDatabase = "boxpvp";
    private String starterKit = "gracz";

    private List<Kit> kits = new ArrayList<>();

    public KitsConfig() {
    }

    public KitsConfig(String id) {
        super(id);
    }

    public Kit findByName(String name) {
        for (Kit kit : this.kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }

    public String getStarterKit() {
        return starterKit;
    }

    public List<Kit> getKits() {
        return kits;
    }

    public String getKitUsersDatabase() {
        return kitUsersDatabase;
    }
}
