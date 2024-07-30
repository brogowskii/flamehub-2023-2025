package io.github.flamehub.kits;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import io.github.flamehub.kits.kit.Kit;
import java.util.ArrayList;
import java.util.List;

@FlameConfigProperties(name = "kits.json")
@EnableRemote(collection = "configs")
public final class KitsConfig extends FlameConfig {

  private String starterKit = "gracz";
  private int rowsGui = 5;

  private List<Kit> kits = new ArrayList<>();

  public KitsConfig() {
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


  public int getRowsGui() {
    return rowsGui;
  }
}
