package io.github.flamehub.commons.bukkit.user;

import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.user.UserCache;
import org.bukkit.command.CommandSender;

public class UserArgument<U extends User> extends ArgumentResolver<CommandSender, U> {

  private final UserCache<U> userCache;
  private final BukkitMessagesService messagesService;

  public UserArgument(final UserCache<U> userCache, final BukkitMessagesService messagesService) {
    this.userCache = userCache;
    this.messagesService = messagesService;
  }

  @Override
  @Async
  protected ParseResult<U> parse(final Invocation<CommandSender> invocation, final Argument<U> context,
      final String argument) {

    final U user = userCache.findByName(argument);
    if (user == null) {
      return ParseResult.failure(
          TextUtil.legacyColor(messagesService.getMessage("user.does.not.exist")));
    }

    return ParseResult.success(user);
  }

  @Override
  public SuggestionResult suggest(final Invocation<CommandSender> invocation, final Argument<U> argument,
      final SuggestionContext context) {
    return userCache.values().stream()
        .map(User::getName)
        .collect(SuggestionResult.collector());
  }
}
