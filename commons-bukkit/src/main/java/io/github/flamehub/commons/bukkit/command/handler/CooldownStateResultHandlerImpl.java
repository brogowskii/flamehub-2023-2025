package io.github.flamehub.commons.bukkit.command.handler;

import dev.rollczi.litecommands.cooldown.CooldownState;
import dev.rollczi.litecommands.cooldown.CooldownStateResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.message.MessageRegistry;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.util.TimeUtil;
import org.bukkit.command.CommandSender;

public final class CooldownStateResultHandlerImpl extends
    CooldownStateResultHandler<CommandSender> {

  public CooldownStateResultHandlerImpl(final MessageRegistry<CommandSender> messageRegistry) {
    super(messageRegistry);
  }

  @Override
  public void handle(final Invocation<CommandSender> invocation, final CooldownState cooldownState,
      final ResultHandlerChain<CommandSender> chain) {
    BukkitMessage.from(
            "&cKolejny raz tą komendę będziesz mógł użyć za: &4" + TimeUtil.formatTimeSimple(
                cooldownState.getRemainingDuration()))
        .deliver(invocation.sender());
  }
}
