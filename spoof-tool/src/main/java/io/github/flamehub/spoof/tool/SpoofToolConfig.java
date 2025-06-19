package io.github.flamehub.spoof.tool;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import org.bukkit.Location;

@FlameConfigProperties(name = "spoof.json")
public final class SpoofToolConfig extends FlameConfig {

  private Location minLocation;
  private Location maxLocation;

  public SpoofToolConfig() {
  }

  public SpoofToolConfig(final Location minLocation, final Location maxLocation) {
    this.minLocation = minLocation;
    this.maxLocation = maxLocation;
  }

  public void setMinLocation(final Location minLocation) {
    this.minLocation = minLocation;
  }

  public void setMaxLocation(final Location maxLocation) {
    this.maxLocation = maxLocation;
  }

  public Location getMinLocation() {
    return minLocation;
  }

  public Location getMaxLocation() {
    return maxLocation;
  }
}
