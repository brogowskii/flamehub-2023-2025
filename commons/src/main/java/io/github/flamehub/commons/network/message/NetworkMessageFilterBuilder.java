package io.github.flamehub.commons.network.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class NetworkMessageFilterBuilder {
    private Collection<UUID> targetPlayers;
    private Collection<String> targetServers;
    private String targetServerCategory;
    private String targetPermission;

    public NetworkMessageFilterBuilder targetPlayers(Collection<UUID> targetPlayers) {
        this.targetPlayers = targetPlayers;
        return this;
    }

    public NetworkMessageFilterBuilder targetPlayer(UUID targetPlayer) {
        if (this.targetPlayers == null) {
            this.targetPlayers = new ArrayList<>();
        }

        this.targetPlayers.add(targetPlayer);
        return this;
    }

    public NetworkMessageFilterBuilder targetServers(Collection<String> targetServers) {
        this.targetServers = targetServers;
        return this;
    }

    public NetworkMessageFilterBuilder targetServer(String targetServer) {
        if (this.targetServers == null) {
            this.targetServers = new ArrayList<>();
        }
        this.targetServers.add(targetServer);
        return this;
    }

    public NetworkMessageFilterBuilder targetServerCategory(String targetServerCategory) {
        this.targetServerCategory = targetServerCategory;
        return this;
    }

    public NetworkMessageFilterBuilder targetPermission(String targetPermission) {
        this.targetPermission = targetPermission;
        return this;
    }

    public NetworkMessageFilter build() {
        return new NetworkMessageFilter(targetPlayers, targetServers, targetServerCategory, targetPermission);
    }
}