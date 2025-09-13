package io.github.flamehub.proxy.core.command.argument;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.proxy.core.ProxyMessages;
import java.util.Optional;

public final class PlayerArgument extends ArgumentResolver<CommandSource, Player> {

  private final ProxyServer proxyServer;
  private final ProxyMessages proxyMessages;

  public PlayerArgument(
      final ProxyServer proxyServer,
      final ProxyMessages proxyMessages) {
    this.proxyServer = proxyServer;
    this.proxyMessages = proxyMessages;
  }

  @Override
  protected ParseResult<Player> parse(
      final Invocation<CommandSource> invocation,
      final Argument<Player> context,
      final String argument) {

    final Optional<Player> player = proxyServer.getPlayer(argument);
    return player.map(ParseResult::success)
        .orElseGet(() -> ParseResult.failure(proxyMessages.playerIsOffline.applyFirst()));

  }

  @Override
  public SuggestionResult suggest(
      final Invocation<CommandSource> invocation,
      final Argument<Player> argument,
      final SuggestionContext context) {

    return proxyServer.getAllPlayers().stream()
        .map(Player::getUsername)
        .collect(SuggestionResult.collector());
  }

}
