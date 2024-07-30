package io.github.flamehub.commons.bukkit.execute;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class ExecutePacket implements Packet {

  private final String command;

  public ExecutePacket(String command) {
    this.command = command;
  }

  public String getCommand() {
    return command;
  }
}
