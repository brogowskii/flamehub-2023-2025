package io.github.flamehub.essentials.privatemessage;

import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.essentials.user.EssentialsUserModule;
import org.bukkit.plugin.Plugin;

public final class PrivateMessageModule extends BukkitModule {

    public PrivateMessageModule(
            final Plugin plugin,
            final FlameDispatcher flameDispatcher,
            final EssentialsUserModule essentialsUserModule
    ) {
        super(plugin, flameDispatcher);

        this.redisMessenger.subscribe("private_messages", new PrivateMessageHandler(this.messagesService, essentialsUserModule));
        this.redisMessenger.subscribe(this.networkServerCache.getCurrent().getName(), new ReplyHandler(essentialsUserModule));

        super.addCommands(
                new PrivateMessageCommand(
                        super.flameDispatcher,
                        super.messagesService,
                        super.redisMessenger,
                        essentialsUserModule
                ),
                new ReplyCommand(
                        super.flameDispatcher,
                        super.networkPlayerCache,
                        super.redisMessenger,
                        super.messagesService,
                        essentialsUserModule
                ),
                new SocialSpyCommand(super.flameDispatcher, super.messagesService, essentialsUserModule)
        );

    }
}
