package io.github.flamehub.commons.network.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class NetworkMessageFilterBuilder {

  private Collection<UUID> targetPlayers;
  private Collection<String> targetServers;
  private String targetServerCategory;
  private String targetPermission;
  private String idForHide;

  public NetworkMessageFilterBuilder targetPlayers(final Collection<UUID> targetPlayers) {
    this.targetPlayers = targetPlayers;
    return this;
  }

  public NetworkMessageFilterBuilder targetPlayer(final UUID targetPlayer) {
    if (targetPlayers == null) {
      targetPlayers = new ArrayList<>();
    }

    targetPlayers.add(targetPlayer);
    return this;
  }

  public NetworkMessageFilterBuilder targetServers(final Collection<String> targetServers) {
    this.targetServers = targetServers;
    return this;
  }

  public NetworkMessageFilterBuilder targetServer(final String targetServer) {
    if (targetServers == null) {
      targetServers = new ArrayList<>();
    }
    targetServers.add(targetServer);
    return this;
  }

  public NetworkMessageFilterBuilder targetServerCategory(final String targetServerCategory) {
    this.targetServerCategory = targetServerCategory;
    return this;
  }

  public NetworkMessageFilterBuilder targetPermission(final String targetPermission) {
    this.targetPermission = targetPermission;
    return this;
  }

  public NetworkMessageFilterBuilder idForHide(final String idForHide) {
    this.idForHide = idForHide;
    return this;
  }

  public NetworkMessageFilter build() {
    return new NetworkMessageFilter(targetPlayers, targetServers, targetServerCategory,
        targetPermission, idForHide);
  }
}