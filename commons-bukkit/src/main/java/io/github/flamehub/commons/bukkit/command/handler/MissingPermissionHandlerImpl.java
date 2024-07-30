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

  public MissingPermissionHandlerImpl(BukkitMessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @Override
  public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions,
      ResultHandlerChain<CommandSender> chain) {
    CommandSender sender = invocation.sender();
    this.messagesService.message("cmd.disallowed.permission")
        .with("permission", missingPermissions.asJoinedText())
        .send(sender);
  }
}