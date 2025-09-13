package io.github.flamehub.commons.bukkit.command.argument;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PlayerArgument extends ArgumentResolver<CommandSender, Player> {

  private final BukkitMessagesService messagesService;

  public PlayerArgument(final BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Override
  protected ParseResult<Player> parse(final Invocation<CommandSender> invocation,
      final Argument<Player> context, final String argument) {
    final Player player = Bukkit.getPlayer(argument);
    if (player != null) {
      return ParseResult.success(player);
    }

    return ParseResult.failure(
        TextUtil.legacyColor(messagesService.getMessage("player.is.offline")));
  }

  @Override
  public SuggestionResult suggest(final Invocation<CommandSender> invocation, final Argument<Player> argument,
      final SuggestionContext context) {
    return Bukkit.getOnlinePlayers().stream()
        .map(Player::getName)
        .collect(SuggestionResult.collector());
  }

}
