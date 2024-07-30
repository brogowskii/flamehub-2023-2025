package io.github.flamehub.proxy.core.command.handler;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import io.github.flamehub.proxy.core.message.VelocityMessagesService;

public final class MissingPermissionHandlerImpl implements
    MissingPermissionsHandler<CommandSource> {

  private final VelocityMessagesService messagesService;

  public MissingPermissionHandlerImpl(VelocityMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Override
  public void handle(Invocation<CommandSource> invocation, MissingPermissions missingPermissions,
      ResultHandlerChain<CommandSource> resultHandlerChain) {
    CommandSource sender = invocation.sender();
    this.messagesService.message("cmd.disallowed.permission")
        .with("permission", missingPermissions.asJoinedText())
        .send(sender);
  }
}