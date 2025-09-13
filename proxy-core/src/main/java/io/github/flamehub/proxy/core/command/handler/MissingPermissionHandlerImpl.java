package io.github.flamehub.proxy.core.command.handler;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import io.github.flamehub.proxy.core.ProxyMessages;

public final class MissingPermissionHandlerImpl implements
    MissingPermissionsHandler<CommandSource> {

  private final ProxyMessages proxyMessages;

  public MissingPermissionHandlerImpl(final ProxyMessages proxyMessages) {
    this.proxyMessages = proxyMessages;
  }

  @Override
  public void handle(
      final Invocation<CommandSource> invocation,
      final MissingPermissions missingPermissions,
      final ResultHandlerChain<CommandSource> resultHandlerChain) {

    final CommandSource sender = invocation.sender();
    proxyMessages
        .insufficientPermissions
        .with("permission", missingPermissions.asJoinedText())
        .deliver(sender);
  }
}