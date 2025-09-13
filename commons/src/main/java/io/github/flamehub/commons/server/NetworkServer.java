package io.github.flamehub.commons.server;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class NetworkServer implements Serializable {

  @Id
  private String name;
  private String category;
  private String ip;

  private NetworkServerStatistics statistics;

  private NetworkServer() {
  }

  public NetworkServer(final String name, final String category, String ip, final NetworkServerStatistics statistics) {
    this.name = name;
    this.category = category;
    this.ip = ip;
    this.statistics = statistics;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public NetworkServerStatistics getStatistics() {
    if (statistics == null) {
      statistics = new NetworkServerStatistics();
    }
    return statistics;
  }

  public void setStatistics(final NetworkServerStatistics statistics) {
    this.statistics = statistics;
  }

  public boolean isOnline() {
    return statistics.getLastUpdate().plus(3, ChronoUnit.SECONDS).isAfter(Instant.now());
  }

  public boolean isOffline() {
    return !isOnline();
  }

  public String getIp() {
    return ip;
  }
}
