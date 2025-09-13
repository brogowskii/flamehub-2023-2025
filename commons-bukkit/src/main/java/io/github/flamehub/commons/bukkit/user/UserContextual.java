package io.github.flamehub.commons.bukkit.user;

import dev.rollczi.litecommands.context.ContextProvider;
import dev.rollczi.litecommands.context.ContextResult;
import dev.rollczi.litecommands.invocation.Invocation;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.user.UserCache;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserContextual<U extends User> implements ContextProvider<CommandSender, U> {

  private final UserCache<U> userCache;

  public UserContextual(final UserCache<U> userCache) {
    this.userCache = userCache;
  }

  @Override
  public ContextResult<U> provide(final Invocation<CommandSender> invocation) {

    final CommandSender sender = invocation.sender();
    if (sender instanceof final Player player) {
      return ContextResult.ok(() -> userCache.findByUniqueId(player.getUniqueId()));
    }

    return ContextResult.error("You need to be a player!");
  }
}
