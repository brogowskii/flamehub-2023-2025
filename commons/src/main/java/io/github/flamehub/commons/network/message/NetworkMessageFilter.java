package io.github.flamehub.commons.network.message;

import java.util.Collection;
import java.util.UUID;

public final class NetworkMessageFilter {

  private final Collection<UUID> targetPlayers;
  private final Collection<String> targetServers;
  private final String targetServerCategory;
  private final String targetPermission;
  private final String idForHide;

  public NetworkMessageFilter(
      final Collection<UUID> targetPlayers,
      final Collection<String> targetServers,
      final String targetServerCategory,
      final String targetPermission,
      final String idForHide
  ) {
    this.targetPlayers = targetPlayers;
    this.targetServers = targetServers;
    this.targetServerCategory = targetServerCategory;
    this.targetPermission = targetPermission;
    this.idForHide = idForHide;
  }

  public static NetworkMessageFilterBuilder builder() {
    return new NetworkMessageFilterBuilder();
  }

  public Collection<UUID> getTargetPlayers() {
    return targetPlayers;
  }

  public Collection<String> getTargetServers() {
    return targetServers;
  }

  public String getTargetServerCategory() {
    return targetServerCategory;
  }

  public String getTargetPermission() {
    return targetPermission;
  }

  public String getIdForHide() {
    return idForHide;
  }
}
