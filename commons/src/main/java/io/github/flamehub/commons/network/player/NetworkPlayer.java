package io.github.flamehub.commons.network.player;

import java.time.Instant;
import java.util.UUID;

public final class NetworkPlayer {

    private final UUID uniqueId;
    private final String name;
    private final Instant joinTime;
    private String server;
    private String proxy;

    private Instant helpopDelay;

    public NetworkPlayer(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.joinTime = Instant.now();
        this.helpopDelay = Instant.ofEpochMilli(0);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public Instant getJoinTime() {
        return joinTime;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public Instant getHelpopDelay() {
        return helpopDelay;
    }

    public void setHelpopDelay(Instant helpopDelay) {
        this.helpopDelay = helpopDelay;
    }
}
