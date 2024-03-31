package io.github.flamehub.essentials.privatemessage;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.essentials.user.EssentialsUser;
import io.github.flamehub.essentials.user.EssentialsUserModule;
import org.bukkit.entity.Player;

@Command(name = "ignore", aliases = "ignoruj")
final class IgnoreCommand {

    private final BukkitMessagesService messagesService;
    private final EssentialsUserModule essentialsUserModule;

    IgnoreCommand(final BukkitMessagesService messagesService, final EssentialsUserModule essentialsUserModule) {
        this.messagesService = messagesService;
        this.essentialsUserModule = essentialsUserModule;
    }

    @Execute(name = "all", aliases = "*")
    void executeAll(@Context final Player player) {
        final EssentialsUser essentialsUser = this.essentialsUserModule.findByUniqueId(player.getUniqueId());
        essentialsUser.setIgnoreAll(!essentialsUser.isIgnoreAll());
        essentialsUser.setNeedUpdate(true);
        this.messagesService.message("ignore.all.status.changed")
                .with("status", essentialsUser.isIgnoreAll() ? "&awłączony" : "&cwyłączony")
                .send(player);
    }

    @Execute
    void execute(@Context final Player player, @Arg("gracz") final NetworkPlayer target) {
        final EssentialsUser essentialsUser = this.essentialsUserModule.findByUniqueId(player.getUniqueId());
        if (essentialsUser.isIgnore(target.getUniqueId())) {
            essentialsUser.removeIgnore(target.getUniqueId());
            this.messagesService.message("ignore.remove")
                    .with("target", target.getName())
                    .send(player);
        }
        else {

            essentialsUser.addIgnore(target.getUniqueId());
            this.messagesService.message("ignore.add")
                    .with("target", target.getName())
                    .send(player);
        }

        essentialsUser.setNeedUpdate(true);
    }


}
