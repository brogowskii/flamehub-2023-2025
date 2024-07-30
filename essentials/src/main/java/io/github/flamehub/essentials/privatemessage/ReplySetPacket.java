package io.github.flamehub.essentials.privatemessage;

import io.github.flamehub.commons.messenger.packet.Packet;
import java.util.UUID;

final class ReplySetPacket implements Packet {

  private final UUID player;
  private final UUID reply;

  ReplySetPacket(final UUID player, final UUID reply) {
    this.player = player;
    this.reply = reply;
  }

  public UUID getPlayer() {
    return player;
  }

  public UUID getReply() {
    return reply;
  }
}
