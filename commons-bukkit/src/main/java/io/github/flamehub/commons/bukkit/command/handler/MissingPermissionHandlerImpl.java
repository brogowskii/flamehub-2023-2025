package io.github.flamehub.commons.bukkit.command.handler;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import org.bukkit.command.CommandSender;

public final class MissingPermissionHandlerImpl implements
    MissingPermissionsHandler<CommandSender> {

  private final BukkitMessagesService messagesService;

  public MissingPermissionHandlerImpl(final BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Override
  public void handle(final Invocation<CommandSender> invocation, final MissingPermissions missingPermissions,
      final ResultHandlerChain<CommandSender> chain) {
    final CommandSender sender = invocation.sender();
    messagesService.message("cmd.disallowed.permission")
        .with("permission", missingPermissions.asJoinedText())
        .deliver(sender);
  }
}