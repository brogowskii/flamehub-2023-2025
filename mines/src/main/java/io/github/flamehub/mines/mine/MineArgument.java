package io.github.flamehub.mines.mine;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;

public final class MineArgument extends ArgumentResolver<CommandSender, Mine> {

  private final MineConfig mineConfig;

  public MineArgument(MineConfig mineConfig) {
    this.mineConfig = mineConfig;
  }

  @Override
  protected ParseResult<Mine> parse(Invocation<CommandSender> invocation, Argument<Mine> argument,
      String s) {

    Mine mine = this.mineConfig.findById(s);
    if (mine == null) {
      return ParseResult.failure("&cNie znaleziono kopalni o takiej nazwie.");
    }

    return ParseResult.success(mine);
  }

  @Override
  public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Mine> argument,
      SuggestionContext context) {
    return SuggestionResult.from(
        this.mineConfig.getMinesById().values()
            .stream()
            .map(Mine::getId)
            .map(Suggestion::of)
            .collect(Collectors.toList())
    );
  }
}
