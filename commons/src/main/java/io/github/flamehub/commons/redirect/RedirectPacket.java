package io.github.flamehub.commons.redirect;

import io.github.flamehub.commons.messenger.packet.Packet;

public class RedirectPacket implements Packet {

  private final String player;
  private final String server;

  public RedirectPacket(String player, String server) {
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
