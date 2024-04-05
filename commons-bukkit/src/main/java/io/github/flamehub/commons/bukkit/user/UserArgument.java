package io.github.flamehub.commons.bukkit.user;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.user.UserDatabaseCache;
import org.bukkit.command.CommandSender;

public class UserArgument<U extends User> extends ArgumentResolver<CommandSender, U> {

    private final UserDatabaseCache<U> userCache;
    private final BukkitMessagesService messagesService;

    public UserArgument(UserDatabaseCache<U> userCache, BukkitMessagesService messagesService) {
        this.userCache = userCache;
        this.messagesService = messagesService;
    }

    @Override
    protected ParseResult<U> parse(Invocation<CommandSender> invocation, Argument<U> context, String argument) {

        U user = this.userCache.findByName(argument);
        if (user == null) {
            return ParseResult.failure(TextUtil.legacyColor(this.messagesService.getMessage("user.does.not.exist")));
        }

        return ParseResult.success(user);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<U> argument, SuggestionContext context) {
        return this.userCache.values().stream()
                .map(User::getName)
                .collect(SuggestionResult.collector());
    }
}
