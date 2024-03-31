package io.github.flamehub.afkzone;

import io.github.flamehub.commons.config.MongoConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.Arrays;
import java.util.List;

public final class AfkZoneConfig extends MongoConfig {

    private Location minLocation = new Location(Bukkit.getWorld("world"), -13.7, 0.0, 11.7);
    private Location maxLocation = new Location(Bukkit.getWorld("world"), 14.7, 0.0, 5.3);

    private List<AfkZoneReward> afkZoneRewards = Arrays.asList(

            new AfkZoneReward(
                    "premium",
                    600,
                    "crate givekey afkbox {PLAYER} 1",
                    "&5✦ &8| &fLosowanie klucza za: &d{TIME} &8(&5{PERCENTAGE}%&8) &8| &fSzansa na wydropienie: &5{CHANCE}%",
                    BarColor.PURPLE,
                    BarStyle.SOLID
            ),
            new AfkZoneReward(
                    "standard",
                    60,
                    "jakaskomenda",
                    "&2✦ &8| &fPodstawową nagrodę otrzymasz za: &a{TIME} &8(&a{PERCENTAGE}%&8)",
                    BarColor.GREEN,
                    BarStyle.SOLID
            )
    );

    public AfkZoneConfig() {
    }

    public AfkZoneConfig(String id) {
        super(id);
    }

    public void setMinLocation(Location minLocation) {
        this.minLocation = minLocation;
    }

    public void setMaxLocation(Location maxLocation) {
        this.maxLocation = maxLocation;
    }

    public Location getMinLocation() {
        return minLocation;
    }

    public Location getMaxLocation() {
        return maxLocation;
    }


    public List<AfkZoneReward> getAfkZoneRewards() {
        return afkZoneRewards;
    }
}
