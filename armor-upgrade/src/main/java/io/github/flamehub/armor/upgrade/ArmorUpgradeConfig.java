package io.github.flamehub.armor.upgrade;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FlameConfigProperties(name = "armorUpgrades.json")
@EnableRemote(collection = "configs")
public final class ArmorUpgradeConfig extends FlameConfig {

  private Map<String, List<ArmorUpgrade>> armorUpgradeTypeListMap = new HashMap<>();
  private Map<String, ArmorUpgradeType> armorUpgradeTypesById = new HashMap<>();


  public ArmorUpgradeConfig() {
  }

  public Map<String, List<ArmorUpgrade>> getArmorUpgradeTypeListMap() {
    return armorUpgradeTypeListMap;
  }

  public Map<String, ArmorUpgradeType> getArmorUpgradeTypesById() {
    return armorUpgradeTypesById;
  }

}
