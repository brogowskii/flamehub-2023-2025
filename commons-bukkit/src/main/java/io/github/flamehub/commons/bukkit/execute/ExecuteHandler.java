package io.github.flamehub.commons.bukkit.execute;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import org.bukkit.Bukkit;

public final class ExecuteHandler {

  private final FlameDispatcher flameDispatcher;

  public ExecuteHandler(FlameDispatcher flameDispatcher) {
    this.flameDispatcher = flameDispatcher;
  }

  @PacketHandler
  public void handle(ExecutePacket executePacket) {
    this.flameDispatcher.dispatch(
        () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), executePacket.getCommand()));
  }

}
