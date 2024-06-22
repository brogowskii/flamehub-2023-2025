package io.github.flamehub.commons.bukkit.network.player;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import org.bukkit.command.CommandSender;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;

import java.util.List;

public final class NetworkPlayerArgument extends ArgumentResolver<CommandSender, NetworkPlayer> {

    private final BukkitMessagesService messagesService;
    private final NetworkPlayerCache networkPlayerCache;
    private final NetworkServer current;

    public NetworkPlayerArgument(BukkitMessagesService messagesService, NetworkPlayerCache networkPlayerCache, NetworkServerCache networkServerCache) {
        this.messagesService = messagesService;
        this.networkPlayerCache = networkPlayerCache;
        this.current = networkServerCache.getCurrent();
    }

    @Override
    protected ParseResult<NetworkPlayer> parse(Invocation<CommandSender> invocation, Argument<NetworkPlayer> context, String argument) {

        NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(argument);
        if (networkPlayer == null || !current.getCategory().equals(networkPlayer.getServerCategory())) {
            return ParseResult.failure(TextUtil.legacyColor(this.messagesService.getMessage("player.is.offline")));
        }

        return ParseResult.success(networkPlayer);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<NetworkPlayer> argument, SuggestionContext context) {
        return SuggestionResult.from(this.networkPlayerCache.values()
                .stream()
                .filter(networkPlayer -> current.getCategory().equals(networkPlayer.getServerCategory()))
                .map(NetworkPlayer::getName)
                .map(Suggestion::of)
                .toList());
    }
}
