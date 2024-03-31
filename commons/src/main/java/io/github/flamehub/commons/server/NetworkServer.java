package io.github.flamehub.commons.server;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity("network_servers")
public final class NetworkServer implements Serializable {

    @Id
    private String name;
    private String category;

    private NetworkServerStatistics statistics;

    private NetworkServer() {
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

    public void setStatistics(NetworkServerStatistics statistics) {
        this.statistics = statistics;
    }

    public boolean isOnline() {
        return this.statistics.getLastUpdate().plus(3, ChronoUnit.SECONDS).isAfter(Instant.now());
    }

    public boolean isOffline() {
        return !isOnline();
    }
}
