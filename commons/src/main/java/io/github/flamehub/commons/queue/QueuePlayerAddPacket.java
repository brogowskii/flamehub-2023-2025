package io.github.flamehub.commons.queue;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class QueuePlayerAddPacket implements Packet {

  private final String player;
  private final String server;

  public QueuePlayerAddPacket(String player, String server) {
    this.player = player;
    this.server = server;
  }

  public String getPlayer() {
    return player;
  }

  public String getServer() {
    return server;
  }
}
