package io.github.flamehub.timeplayed.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.UserUpdatable;

import java.util.UUID;

@Entity("time_played_users")
public final class TimePlayedUser extends UserUpdatable {

    private int coins;
    private long spendTime;

    private long lastAddCoinsTimeMeasurement;
    private long lastSpendTimeMeasurement;

    public TimePlayedUser() {

    }

    public TimePlayedUser(UUID uniqueId, String name) {
        super(uniqueId, name);
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public long getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(long spendTime) {
        this.spendTime = spendTime;
    }

    public long getLastAddCoinsTimeMeasurement() {
        return lastAddCoinsTimeMeasurement;
    }

    public void setLastAddCoinsTimeMeasurement(long lastAddCoinsTimeMeasurement) {
        this.lastAddCoinsTimeMeasurement = lastAddCoinsTimeMeasurement;
    }

    public long getLastSpendTimeMeasurement() {
        return lastSpendTimeMeasurement;
    }

    public void setLastSpendTimeMeasurement(long lastSpendTimeMeasurement) {
        this.lastSpendTimeMeasurement = lastSpendTimeMeasurement;
    }
}
