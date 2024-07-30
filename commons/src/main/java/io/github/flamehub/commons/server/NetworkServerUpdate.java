package io.github.flamehub.commons.server;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class NetworkServerUpdate implements Packet {

  private final String name;
  private final int players;
  private final int playersLimit;
  private final boolean frozen;
  private final double[] tps;


  public NetworkServerUpdate(String name, int players, int playersLimit, boolean frozen,
      double[] tps) {
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
