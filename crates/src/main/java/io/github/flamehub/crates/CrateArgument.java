package io.github.flamehub.crates;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

public final class CrateArgument extends ArgumentResolver<CommandSender, Crate> {

  private final CratesConfig cratesConfig;

  public CrateArgument(CratesConfig cratesConfig) {
    this.cratesConfig = cratesConfig;
  }

  @Override
  protected ParseResult<Crate> parse(Invocation<CommandSender> invocation, Argument<Crate> context,
      String argument) {
    Crate byId = cratesConfig.findById(argument);
    if (byId == null) {
      return ParseResult.failure("&cSkrzynia o podanym id nie istnieje.");
    }

    return ParseResult.success(byId);
  }

  @Override
  public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Crate> argument,
      SuggestionContext context) {
    return SuggestionResult.from(cratesConfig.getCrates()
        .stream()
        .map(Crate::getId)
        .map(Suggestion::of)
        .toList());
  }
}
