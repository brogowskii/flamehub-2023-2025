package io.github.flamehub.proxy.core.motd;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.util.TextUtil;

@Command(name = "motd")
@Permission("server.velocity.commands.motd")
public final class MotdCommand {

  private final FlameConfigService flameConfigService;

  public MotdCommand(final FlameConfigService flameConfigService) {
    this.flameConfigService = flameConfigService;
  }

  @Execute(name = "update")
  void update(final @Context CommandSource commandSource) throws IllegalAccessException {
    flameConfigService.update(MotdConfig.class);
    VelocityMessage.from("&aSuccessfully updated motd configuration.")
        .deliver(commandSource);
  }

  @Execute(name = "reload")
  void reload(final @Context CommandSource commandSource) throws IllegalAccessException {
    flameConfigService.refreshLocally(MotdConfig.class);
    VelocityMessage.from("&aSuccessfully reloaded motd configuration.")
        .deliver(commandSource);
  }

}
