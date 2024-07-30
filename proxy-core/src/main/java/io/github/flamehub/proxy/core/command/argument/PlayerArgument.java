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
import io.github.flamehub.commons.message.MessagesService;
import java.util.Optional;

public class PlayerArgument extends ArgumentResolver<CommandSource, Player> {

  private final ProxyServer proxyServer;
  private final MessagesService messagesService;

  public PlayerArgument(ProxyServer proxyServer, MessagesService messagesService) {
    this.proxyServer = proxyServer;
    this.messagesService = messagesService;
  }

  @Override
  protected ParseResult<Player> parse(Invocation<CommandSource> invocation,
      Argument<Player> context, String argument) {
    Optional<Player> player = this.proxyServer.getPlayer(argument);

    return player.map(ParseResult::success)
        .orElseGet(() -> ParseResult.failure(this.messagesService.getMessage("player.is.offline")));

  }

  @Override
  public SuggestionResult suggest(Invocation<CommandSource> invocation, Argument<Player> argument,
      SuggestionContext context) {
    return this.proxyServer.getAllPlayers().stream()
        .map(Player::getUsername)
        .collect(SuggestionResult.collector());
  }

}
