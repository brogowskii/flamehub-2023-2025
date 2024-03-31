package io.github.flamehub.essentials.privatemessage;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.essentials.user.EssentialsUser;
import io.github.flamehub.essentials.user.EssentialsUserModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

final class PrivateMessageHandler {

    private final BukkitMessagesService messagesService;
    private final EssentialsUserModule essentialsUserModule;

    PrivateMessageHandler(BukkitMessagesService messagesService, EssentialsUserModule essentialsUserModule) {
        this.messagesService = messagesService;
        this.essentialsUserModule = essentialsUserModule;
    }

    @PacketHandler
    void handle(PrivateMessage message) {

        final String receiver = message.getReceiver();
        final Player player = Bukkit.getPlayer(receiver);
        if (player != null) {
            this.messagesService.message("msg.broadcast")
                    .with("from", message.getSender())
                    .with("to", "Ja")
                    .with("message", message.getMessage())
                    .send(player);
        }

        this.essentialsUserModule.values()
                .stream()
                .filter(EssentialsUser::isSocialSpy)
                .forEach(coreUser -> {
                    final Player socialSpyPlayer = Bukkit.getPlayer(coreUser.getUniqueId());
                    if (socialSpyPlayer == null) {
                        return;
                    }

                    this.messagesService.message("msg.broadcast.socialspy")
                            .with("from", message.getSender())
                            .with("to", receiver)
                            .with("message", message.getMessage())
                            .send(socialSpyPlayer);
                });

    }

}
