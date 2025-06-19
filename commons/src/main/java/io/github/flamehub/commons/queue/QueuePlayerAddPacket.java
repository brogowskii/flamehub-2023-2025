package io.github.flamehub.commons.queue;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class QueuePlayerAddPacket implements Packet {

  private String player;
  private String server;

  public QueuePlayerAddPacket() {
  }

  public QueuePlayerAddPacket(final String player, final String server) {
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
