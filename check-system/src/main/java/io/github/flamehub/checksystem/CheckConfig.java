package io.github.flamehub.checksystem;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@FlameConfigProperties(name = "check.json")
public final class CheckConfig extends FlameConfig {

  private Location location = new Location(Bukkit.getWorld("world"), -15.5, 191, 199.5);

  private final String admitPunishment = "tempban {PLAYER} 3d Przyznanie się do cheatów.";
  private final String noCooperationPunishment = "tempban {PLAYER} 7d Brak współpracy podczas sprawdzania.";
  private final String logoutPunishment = "tempban {PLAYER} 7d Wylogowanie się podczas sprawdzania.";
  private final String cheatingPunishment = "tempban {PLAYER} 14d Wykrycie cheatów podczas sprawdzania.";

  public String getAdmitPunishment() {
    return admitPunishment;
  }

  public String getNoCooperationPunishment() {
    return noCooperationPunishment;
  }

  public String getCheatingPunishment() {
    return cheatingPunishment;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public String getLogoutPunishment() {
    return logoutPunishment;
  }
}
