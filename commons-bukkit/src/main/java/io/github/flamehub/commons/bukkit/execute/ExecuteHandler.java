package io.github.flamehub.commons.bukkit.execute;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import org.bukkit.Bukkit;

public final class ExecuteHandler {

  private final FlameDispatcher flameDispatcher;

  public ExecuteHandler(final FlameDispatcher flameDispatcher) {
    this.flameDispatcher = flameDispatcher;
  }

  @PacketHandler
  public void handle(final ExecutePacket executePacket) {
    flameDispatcher.dispatch(
        () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executePacket.getCommand()));
  }

}
