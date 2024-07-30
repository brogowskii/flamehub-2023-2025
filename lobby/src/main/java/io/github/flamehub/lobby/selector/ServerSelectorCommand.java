package io.github.flamehub.lobby.selector;

import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresher;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;

@Command(name = "serverselector")
@Permission("server.commands.serverselector")
public final class ServerSelectorCommand extends FlameConfigRefresher {

  private final FlameConfigService flameConfigService;

  public ServerSelectorCommand(FlameConfigService flameConfigService) {
    super(flameConfigService, ServerSelectorConfig.class);
    this.flameConfigService = flameConfigService;
  }

  @Async
  @Execute(name = "reload")
  void execute(@Context CommandSender sender) {
    super.refreshConfigLocally(sender);
  }


}
