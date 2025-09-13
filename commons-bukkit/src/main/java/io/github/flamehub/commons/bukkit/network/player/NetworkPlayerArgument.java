package io.github.flamehub.commons.bukkit.network.player;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import org.bukkit.command.CommandSender;

public final class NetworkPlayerArgument extends ArgumentResolver<CommandSender, NetworkPlayer> {

  private final BukkitMessagesService messagesService;
  private final NetworkPlayerCache networkPlayerCache;
  private final NetworkServer current;

  public NetworkPlayerArgument(
      final BukkitMessagesService messagesService,
      final NetworkPlayerCache networkPlayerCache,
      final NetworkServerFacade networkServerFacade
  ) {
    this.messagesService = messagesService;
    this.networkPlayerCache = networkPlayerCache;
    current = networkServerFacade.getCurrent();
  }

  @Override
  protected ParseResult<NetworkPlayer> parse(final Invocation<CommandSender> invocation,
      final Argument<NetworkPlayer> context, final String argument) {

    final NetworkPlayer networkPlayer = networkPlayerCache.findByName(argument);
    if (networkPlayer == null || !current.getCategory().equals(networkPlayer.getServerCategory())) {
      return ParseResult.failure(
          TextUtil.legacyColor(messagesService.getMessage("player.is.offline")));
    }

    return ParseResult.success(networkPlayer);
  }

  @Override
  public SuggestionResult suggest(final Invocation<CommandSender> invocation,
      final Argument<NetworkPlayer> argument, final SuggestionContext context) {
    return SuggestionResult.from(networkPlayerCache.values()
        .stream()
        .filter(networkPlayer -> current.getCategory().equals(networkPlayer.getServerCategory()))
        .map(NetworkPlayer::getName)
        .map(Suggestion::of)
        .toList());
  }
}
