package io.github.flamehub.commons.config;

import io.github.flamehub.commons.messenger.packet.PacketHandler;

public final class RemoteUpdateHandler {

    private final FlameConfigService flameConfigService;

    public RemoteUpdateHandler(final FlameConfigService flameConfigService) {
        this.flameConfigService = flameConfigService;
    }

    @PacketHandler
    public void handle(final RemoteUpdate remoteUpdate) {
        final Class<? extends FlameConfig> configClazz = this.flameConfigService
                .getConfigInstancesByClassName()
                .get(remoteUpdate.getConfigClassName());

        if (configClazz == null) {
            return;
        }


    }

}
