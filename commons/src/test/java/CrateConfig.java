import io.github.flamehub.commons.legacy.config.MongoConfig;
import io.github.flamehub.commons.legacy.config.MongoConfigProperties;

import java.util.ArrayList;
import java.util.List;

@MongoConfigProperties(collection = "crate_configs")
public final class CrateConfig extends MongoConfig {

    private List<String> testStringList = new ArrayList<>(List.of("wewe", "gwewge", "gewgwe"));
    private int xd = 10;
    private String pet = "Nocek";

    public CrateConfig() {
    }

    public CrateConfig(String id) {
        super(id);
    }
}
