package io.github.flamehub.essentials.privatemessage;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.essentials.user.EssentialsUser;
import io.github.flamehub.essentials.user.EssentialsUserFacade;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PrivateMessageHandler {

  private final BukkitMessagesService messagesService;
  private final EssentialsUserFacade essentialsUserFacade;

  public PrivateMessageHandler(BukkitMessagesService messagesService,
      EssentialsUserFacade essentialsUserFacade) {
    this.messagesService = messagesService;
    this.essentialsUserFacade = essentialsUserFacade;
  }

  @PacketHandler
  public void handle(PrivateMessage message) {

    final String receiver = message.getReceiver();
    final Player player = Bukkit.getPlayer(receiver);
    if (player != null) {
      messagesService.message("msg.broadcast")
          .with("from", message.getSender())
          .with("to", "Ja")
          .with("message", message.getMessage())
          .deliver(player);
    }

    essentialsUserFacade.values()
        .stream()
        .filter(EssentialsUser::isSocialSpy)
        .forEach(coreUser -> {
          final Player socialSpyPlayer = Bukkit.getPlayer(coreUser.getUniqueId());
          if (socialSpyPlayer == null) {
            return;
          }

          messagesService.message("msg.broadcast.socialspy")
              .with("from", message.getSender())
              .with("to", receiver)
              .with("message", message.getMessage())
              .deliver(socialSpyPlayer);
        });

  }

}
