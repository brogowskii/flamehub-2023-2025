package io.github.flamehub.essentials.command.argument;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GameModeArgument extends ArgumentResolver<CommandSender, GameMode> {

    private final BukkitMessagesService messagesService;

    public GameModeArgument(final BukkitMessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    protected ParseResult<GameMode> parse(
            final Invocation<CommandSender> invocation,
            final Argument<GameMode> argument,
            final String s
    ) {
        int value;
        try {
            value = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return ParseResult.failure(TextUtil.legacyColor(this.messagesService.getMessage("gamemode.not.found")));
        }

        final GameMode gameMode = getGameMode(value);
        if (gameMode == null) {
            return ParseResult.failure(TextUtil.legacyColor(this.messagesService.getMessage("gamemode.not.found")));
        }

        return ParseResult.success(gameMode);
    }

    @Override
    public SuggestionResult suggest(
            final Invocation<CommandSender> invocation,
            final Argument<GameMode> argument,
            final SuggestionContext context
    ) {
        return SuggestionResult.from(
                Stream.of("0", "1", "2", "3")
                        .map(Suggestion::of)
                        .collect(Collectors.toSet())
        );
    }

    GameMode getGameMode(int i) {
        for (final GameMode value : GameMode.values()) {
            if (value.getValue() == i) {
                return value;
            }
        }

        return null;
    }
}
