package io.github.flamehub.kits.kit;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.kits.KitsConfig;
import org.bukkit.command.CommandSender;

public final class KitArgument extends ArgumentResolver<CommandSender, Kit> {

  private final KitsConfig kitsConfig;

  public KitArgument(final KitsConfig kitsConfig) {
    this.kitsConfig = kitsConfig;
  }

  @Override
  protected ParseResult<Kit> parse(final Invocation<CommandSender> invocation, final Argument<Kit> context,
      final String argument) {

    final Kit kit = kitsConfig.findByName(argument);
    if (kit == null) {
      return ParseResult.failure(TextBuilder.builder()
          .text("&cZestaw o podanej nazwie nie istnieje.")
          .firstLine());
    }

    return ParseResult.success(kit);
  }

  @Override
  public SuggestionResult suggest(final Invocation<CommandSender> invocation, final Argument<Kit> argument,
      final SuggestionContext context) {
    return SuggestionResult.from(kitsConfig.getKits()
        .stream()
        .map(Kit::getName)
        .map(Suggestion::of)
        .toList());
  }
}