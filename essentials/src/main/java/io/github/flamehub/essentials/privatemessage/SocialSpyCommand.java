package io.github.flamehub.essentials.privatemessage;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.essentials.user.EssentialsUser;
import io.github.flamehub.essentials.user.EssentialsUserFacade;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@Command(name = "socialspy")
@Permission("server.skypvp.commands.socialspy")
final class SocialSpyCommand {

    private final FlameDispatcher flameDispatcher;
    private final BukkitMessagesService messagesService;
    private final EssentialsUserFacade essentialsUserFacade;

    SocialSpyCommand(
            final FlameDispatcher flameDispatcher,
            final BukkitMessagesService messagesService,
            final EssentialsUserFacade essentialsUserFacade
    ) {
        this.flameDispatcher = flameDispatcher;
        this.messagesService = messagesService;
        this.essentialsUserFacade = essentialsUserFacade;
    }

    @Execute
    void execute(@Context final Player player) {

        final EssentialsUser essentialsUser = this.essentialsUserFacade.findByUniqueId(player.getUniqueId());
        essentialsUser.setSocialSpy(!essentialsUser.isSocialSpy());
        CompletableFuture.supplyAsync(() -> this.essentialsUserFacade.save(essentialsUser))
                .thenAccept(essUser -> {
                    this.messagesService.sendMessage(player, essentialsUser.isSocialSpy() ? "socialspy.on" : "socialspy.off");
                });


    }

}
