package io.github.flamehub.essentials.privatemessage;

import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.essentials.user.EssentialsUser;
import io.github.flamehub.essentials.user.EssentialsUserModule;

final class ReplyHandler {

    private final EssentialsUserModule essentialsUserModule;

    ReplyHandler(final EssentialsUserModule essentialsUserModule) {
        this.essentialsUserModule = essentialsUserModule;
    }

    @PacketHandler
    public void handle(final ReplySetPacket packet) {
        final EssentialsUser coreUser = this.essentialsUserModule.findByUniqueId(packet.getPlayer());
        coreUser.setReply(packet.getReply());
    }

}
