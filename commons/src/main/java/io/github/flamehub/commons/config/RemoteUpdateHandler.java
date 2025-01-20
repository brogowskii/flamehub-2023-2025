package io.github.flamehub.commons.config;

import io.github.flamehub.commons.messenger.packet.PacketHandler;

public final class RemoteUpdateHandler {

  private final FlameConfigService flameConfigService;

  public RemoteUpdateHandler(final FlameConfigService flameConfigService) {
    this.flameConfigService = flameConfigService;
  }

  @PacketHandler
  public void handle(final RemoteUpdate remoteUpdate) throws IllegalAccessException {
    System.out.println("handled update from remote: " + remoteUpdate.getConfigClassName());
    final Class<? extends FlameConfig> flameConfigClazz = flameConfigService
        .getConfigInstancesByClassName()
        .get(remoteUpdate.getConfigClassName()).getClass();

    flameConfigService.refresh(flameConfigClazz, false);
    flameConfigService.saveLocally(flameConfigClazz);
    System.out.println("updated config: " + flameConfigClazz.getName() + " from remote");
  }

}
