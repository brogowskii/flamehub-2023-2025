package io.github.flamehub.commons.bukkit.user;

import dev.rollczi.litecommands.context.ContextProvider;
import dev.rollczi.litecommands.context.ContextResult;
import dev.rollczi.litecommands.invocation.Invocation;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.user.UserDatabaseCache;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserContextual<U extends User> implements ContextProvider<CommandSender, U> {

    private final UserDatabaseCache<U> userCache;

    public UserContextual(UserDatabaseCache<U> userCache) {
        this.userCache = userCache;
    }

    @Override
    public ContextResult<U> provide(Invocation<CommandSender> invocation) {

        CommandSender sender = invocation.sender();
        if (sender instanceof Player player) {
            return ContextResult.ok(() -> userCache.findByUniqueId(player.getUniqueId()));
        }

        return ContextResult.error("You need to be a player!");
    }
}
