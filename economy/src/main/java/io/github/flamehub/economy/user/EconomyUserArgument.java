package io.github.flamehub.economy.user;

import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.user.UserArgument;
import io.github.flamehub.commons.user.UserDatabaseCache;

final class EconomyUserArgument extends UserArgument<EconomyUser> {
    EconomyUserArgument(final UserDatabaseCache<EconomyUser> userCache, final BukkitMessagesService messagesService) {
        super(userCache, messagesService);
    }
}
