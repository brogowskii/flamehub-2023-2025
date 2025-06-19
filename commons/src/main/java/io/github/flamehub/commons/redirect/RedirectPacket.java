package io.github.flamehub.commons.redirect;

import io.github.flamehub.commons.messenger.packet.Packet;

public class RedirectPacket implements Packet {

  private String player;
  private String server;

  public RedirectPacket() {
  }

  public RedirectPacket(final String player, final String server) {
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
