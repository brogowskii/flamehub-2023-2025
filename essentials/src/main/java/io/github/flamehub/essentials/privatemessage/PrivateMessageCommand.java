package io.github.flamehub.essentials.privatemessage;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.essentials.user.EssentialsUser;
import io.github.flamehub.essentials.user.EssentialsUserModule;
import org.bukkit.entity.Player;

@Command(name = "msg", aliases = {"message", "tell", "whisper", "w", "t", "pm", "privatemessage"})
final class PrivateMessageCommand {

    private final FlameDispatcher flameDispatcher;
    private final BukkitMessagesService messagesService;
    private final RedisMessenger redisMessenger;
    private final EssentialsUserModule essentialsUserModule;

    PrivateMessageCommand(
            final FlameDispatcher flameDispatcher,
            final BukkitMessagesService messagesService,
            final RedisMessenger redisMessenger,
            final EssentialsUserModule essentialsUserModule
    ) {
        this.flameDispatcher = flameDispatcher;
        this.messagesService = messagesService;
        this.redisMessenger = redisMessenger;
        this.essentialsUserModule = essentialsUserModule;
    }

    @Execute
    void execute(
            @Context final Player player,
            @Arg("gracz") final NetworkPlayer networkPlayer,
            @Join("wiadomość") final String message
    ) {
        this.flameDispatcher.dispatchAsync(() -> {

            if (player.getUniqueId().equals(networkPlayer.getUniqueId())) {
                return;
            }

            final EssentialsUser essentialsUser = this.essentialsUserModule.findByUniqueId(player.getUniqueId());
            if (essentialsUser.isIgnoreAll() || essentialsUser.isIgnore(networkPlayer.getUniqueId())) {
                this.messagesService.sendMessage(player, "msg.cant.write.with.ignore.all.self");
                return;
            }

            final EssentialsUser targetCoreUser = this.essentialsUserModule.findByUniqueId(networkPlayer.getUniqueId());
            if (targetCoreUser == null) {
                this.messagesService.sendMessage(player, "user.does.not.exist");
                return;
            }

            if (targetCoreUser.isIgnoreAll() || targetCoreUser.isIgnore(player.getUniqueId())) {
                this.messagesService.sendMessage(player, "msg.cant.write.with.ignore.all.target");
                return;
            }

            essentialsUser.setReply(networkPlayer.getUniqueId());
            this.redisMessenger.publish(networkPlayer.getServer(), new ReplySetPacket(networkPlayer.getUniqueId(), player.getUniqueId()));

            this.messagesService.message("msg.broadcast")
                    .with("from", "Ja")
                    .with("to", networkPlayer.getName())
                    .with("message", message.replaceAll("&", ""))
                    .send(player);
            this.redisMessenger.publish("private_messages", new PrivateMessage(player.getName(), networkPlayer.getName(), message));

        });
    }

}
