package io.github.flamehub.commons.server;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;

import java.time.Instant;

@Entity
public final class NetworkServerStatistics {

    @Transient
    private Instant lastUpdate = Instant.now();

    @Transient
    private double[] tps = new double[4];

    @Transient
    private int players;

    private boolean frozen;
    private int playersLimit;

    public NetworkServerStatistics() {

    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public double[] getTps() {
        return tps;
    }

    public void setTps(double[] tps) {
        this.tps = tps;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public int getPlayersLimit() {
        return playersLimit;
    }

    public void setPlayersLimit(int playersLimit) {
        this.playersLimit = playersLimit;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
}
