package io.github.flamehub.commons.server;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class NetworkServerUpdate implements Packet {

  private String name;
  private int players;
  private int playersLimit;
  private boolean frozen;
  private double[] tps;

  public NetworkServerUpdate() {
  }

  public NetworkServerUpdate(
      final String name,
      final int players,
      final int playersLimit,
      final boolean frozen,
      final double[] tps) {
    this.name = name;
    this.players = players;
    this.playersLimit = playersLimit;
    this.frozen = frozen;
    this.tps = tps;
  }

  public String getName() {
    return name;
  }

  public int getPlayers() {
    return players;
  }

  public int getPlayersLimit() {
    return playersLimit;
  }

  public double[] getTps() {
    return tps;
  }

  public boolean isFrozen() {
    return frozen;
  }

}
