package io.github.flamehub.proxy.core.command.handler;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import io.github.flamehub.proxy.core.ProxyMessages;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import java.util.List;

public final class InvalidUsageHandlerImpl implements InvalidUsageHandler<CommandSource> {

  private final ProxyMessages proxyMessages;

  public InvalidUsageHandlerImpl(final ProxyMessages proxyMessages) {
    this.proxyMessages = proxyMessages;
  }

  @Override
  public void handle(
      final Invocation<CommandSource> invocation,
      final InvalidUsage<CommandSource> result,
      final ResultHandlerChain<CommandSource> resultHandlerChain) {

    final CommandSource sender = invocation.sender();
    final List<String> schematics = result.getSchematic().all();

    final VelocityMessage correctUsage = proxyMessages.correctUsage;
    final String usage = schematics.getFirst();
    if (schematics.size() == 1) {
      correctUsage.with("usage", usage).deliver(sender);
      return;
    }

    correctUsage.with("usage", "").deliver(sender);
    for (final String sch : schematics) {
      proxyMessages.correctUsageMultiple
          .with("correct_usage", sch)
          .deliver(sender);
    }
  }
}