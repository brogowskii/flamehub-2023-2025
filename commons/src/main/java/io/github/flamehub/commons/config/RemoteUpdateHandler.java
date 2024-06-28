package io.github.flamehub.commons.config;

import io.github.flamehub.commons.messenger.packet.PacketHandler;

public final class RemoteUpdateHandler {

    private final FlameConfigService flameConfigService;

    public RemoteUpdateHandler(final FlameConfigService flameConfigService) {
        this.flameConfigService = flameConfigService;
    }

    @PacketHandler
    public void handle(final RemoteUpdate remoteUpdate) throws IllegalAccessException {
        final FlameConfig flameConfig = this.flameConfigService
                .getConfigInstancesByClassName()
                .get(remoteUpdate.getConfigClassName());

        if (flameConfig == null) {
            return;
        }

        this.flameConfigService.refresh(flameConfig.getClass(), false);
        this.flameConfigService.saveLocally(flameConfig);
        System.out.println("updated config: " + flameConfig.getClass().getName() + " from remote");
    }

}
