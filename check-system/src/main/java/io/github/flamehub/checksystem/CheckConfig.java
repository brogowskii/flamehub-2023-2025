package io.github.flamehub.checksystem;

import eu.okaeri.configs.OkaeriConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class CheckConfig extends OkaeriConfig {

    private Location location = new Location(Bukkit.getWorld("world"), -15.5, 191, 199.5);

    private String admitPunishment = "tempban {PLAYER} 3d Przyznanie się do cheatów.";
    private String noCooperationPunishment = "tempban {PLAYER} 7d Brak współpracy podczas sprawdzania.";
    private String logoutPunishment = "tempban {PLAYER} 7d Wylogowanie się podczas sprawdzania.";
    private String cheatingPunishment = "tempban {PLAYER} 10d Wykrycie cheatów podczas sprawdzania.";


    public void setLocation(Location location) {
        this.location = location;
    }

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

    public String getLogoutPunishment() {
        return logoutPunishment;
    }
}
