package io.github.flamehub.mines.mine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.Map;

@FlameConfigProperties(name = "mines.json")
public final class MineConfig extends FlameConfig {

  private final Map<String, Mine> minesById = Maps.newHashMap();

  @JsonIgnore
  private final Map<Long, Mine> minesByLocation = Maps.newHashMap();

  public MineConfig() {
  }

  public void add(Mine mine) {
    minesById.put(mine.getId().toLowerCase(), mine);
  }

  public void remove(Mine mine) {
    minesById.remove(mine.getId().toLowerCase());
  }

  public Mine findById(String id) {
    return minesById.get(id.toLowerCase());
  }

  public Map<String, Mine> getMinesById() {
    return minesById;
  }

  public Map<Long, Mine> getMinesByLocation() {
    return minesByLocation;
  }
}
