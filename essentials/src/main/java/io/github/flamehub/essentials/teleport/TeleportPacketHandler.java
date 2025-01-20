package io.github.flamehub.essentials.teleport;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.messenger.packet.PacketHandler;

public final class TeleportPacketHandler {

  private final RedisMessenger redisMessenger;
  private final TeleportFacade teleportFacade;

  TeleportPacketHandler(final RedisMessenger redisMessenger, final TeleportFacade teleportFacade) {
    this.redisMessenger = redisMessenger;
    this.teleportFacade = teleportFacade;
  }

  @PacketHandler
  public void handle(TeleportPacketRequest packet) {
    teleportFacade.add(packet.getRequesterUUID(), packet.getTargetName());
    redisMessenger.publish("callbacks", new TeleportPacketResponse(packet.getUniqueId()));
  }

}
