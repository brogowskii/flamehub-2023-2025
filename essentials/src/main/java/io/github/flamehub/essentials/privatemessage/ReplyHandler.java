package io.github.flamehub.essentials.privatemessage;

import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.essentials.user.EssentialsUser;
import io.github.flamehub.essentials.user.EssentialsUserFacade;

final class ReplyHandler {

    private final EssentialsUserFacade essentialsUserFacade;

    ReplyHandler(final EssentialsUserFacade essentialsUserFacade) {
        this.essentialsUserFacade = essentialsUserFacade;
    }

    @PacketHandler
    public void handle(final ReplySetPacket packet) {
        final EssentialsUser coreUser = this.essentialsUserFacade.findByUniqueId(packet.getPlayer());
        coreUser.setReply(packet.getReply());
    }

}
