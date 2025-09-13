package io.github.flamehub.commons.server;

import dev.morphia.annotations.Transient;
import java.time.Instant;

public final class NetworkServerStatistics {

  private Instant lastUpdate = Instant.now();
  private double[] tps = new double[4];
  private int players;

  public NetworkServerStatistics() {

  }

  public Instant getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(final Instant lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public double[] getTps() {
    return tps;
  }

  public void setTps(final double[] tps) {
    this.tps = tps;
  }

  public int getPlayers() {
    return players;
  }

  public void setPlayers(final int players) {
    this.players = players;
  }


}
