package io.github.flamehub.commons.bukkit.command.argument;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public final class WorldArgument extends ArgumentResolver<CommandSender, World> {

  @Override
  protected ParseResult<World> parse(final Invocation<CommandSender> invocation, final Argument<World> context,
      final String argument) {
    final World world = Bukkit.getWorld(argument);
    if (world == null) {
      return ParseResult.failure("World '" + argument + "' not exists");
    }

    return ParseResult.success(world);
  }

  @Override
  public SuggestionResult suggest(final Invocation<CommandSender> invocation, final Argument<World> argument,
      final SuggestionContext context) {
    return Bukkit.getWorlds().stream()
        .map(World::getName)
        .collect(SuggestionResult.collector());
  }

}