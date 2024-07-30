package io.github.flamehub.essentials.privatemessage;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.essentials.user.EssentialsUser;
import io.github.flamehub.essentials.user.EssentialsUserFacade;
import org.bukkit.entity.Player;

@Command(name = "reply", aliases = {"r"})
final class ReplyCommand {

  private final FlameDispatcher flameDispatcher;
  private final NetworkPlayerCache networkPlayerCache;
  private final RedisMessenger redisMessenger;
  private final BukkitMessagesService messagesService;
  private final EssentialsUserFacade essentialsUserFacade;

  ReplyCommand(
      final FlameDispatcher flameDispatcher,
      final NetworkPlayerCache networkPlayerCache,
      final RedisMessenger redisMessenger,
      final BukkitMessagesService messagesService,
      final EssentialsUserFacade essentialsUserFacade
  ) {
    this.flameDispatcher = flameDispatcher;
    this.networkPlayerCache = networkPlayerCache;
    this.redisMessenger = redisMessenger;
    this.messagesService = messagesService;
    this.essentialsUserFacade = essentialsUserFacade;
  }

  @Execute
  void execute(@Context final Player player, @Join("wiadomość") final String message) {
    this.flameDispatcher.dispatchAsync(() -> {

      final EssentialsUser essentialsUser = this.essentialsUserFacade.findByUniqueId(
          player.getUniqueId());

      if (essentialsUser.getReply() == null) {
        this.messagesService.sendMessage(player, "msg.cant.reply");
        return;
      }

      final NetworkPlayer targetPlayer = this.networkPlayerCache.findByUniqueId(
          essentialsUser.getReply());
      if (targetPlayer == null) {
        this.messagesService.sendMessage(player, "msg.cant.reply");
        return;
      }

      if (essentialsUser.isIgnore(essentialsUser.getReply()) || essentialsUser.isIgnoreAll()) {
        this.messagesService.sendMessage(player, "msg.cant.write.with.ignore.all.self");
        return;
      }

      final EssentialsUser replyUser = this.essentialsUserFacade.findByUniqueId(
          essentialsUser.getReply());
      if (replyUser.isIgnore(player.getUniqueId()) || replyUser.isIgnoreAll()) {
        this.messagesService.sendMessage(player, "msg.cant.write.with.ignore.all.target");
        return;
      }

      this.redisMessenger.publish(targetPlayer.getServer(),
          new ReplySetPacket(targetPlayer.getUniqueId(), player.getUniqueId()));
      this.messagesService.message("msg.broadcast")
          .with("from", "Ja")
          .with("to", replyUser.getName())
          .with("message", message.replaceAll("&", ""))
          .send(player);

      this.redisMessenger.publish("private_messages",
          new PrivateMessage(player.getName(), targetPlayer.getName(), message));

    });


  }


}
