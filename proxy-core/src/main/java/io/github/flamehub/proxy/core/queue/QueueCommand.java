package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.util.TextUtil;
import java.util.List;

@Command(name = "queue")
@Permission("server.velocity.commands.queue")
public final class QueueCommand {

  private final ProxyServer proxyServer;
  private final FlameConfigService flameConfigService;
  private final QueueConfig queueConfig;
  private final QueueService queueService;
  private final NetworkServerFacade networkServerFacade;
  private final QueueRedirectService queueRedirectService;

  public QueueCommand(
      final ProxyServer proxyServer,
      final FlameConfigService flameConfigService,
      final QueueConfig queueConfig,
      final QueueService queueService,
      final NetworkServerFacade networkServerFacade,
      final QueueRedirectService queueRedirectService) {
    this.proxyServer = proxyServer;
    this.flameConfigService = flameConfigService;
    this.queueConfig = queueConfig;
    this.queueService = queueService;
    this.networkServerFacade = networkServerFacade;
    this.queueRedirectService = queueRedirectService;
  }

  @Execute(name = "setPlayersPerMove")
  void setPlayersPerMove(@Context final CommandSource commandSource, @Arg final int playersPerMove)
      throws IllegalAccessException {
    queueConfig.setPlayersPerMove(playersPerMove);
    flameConfigService.save(QueueConfig.class);
    flameConfigService.refreshAndBroadcast(QueueConfig.class);
    VelocityMessage.from("&aUstawiono ilość graczy na przeniesienie: &2" + playersPerMove)
        .deliver(commandSource);
  }

  @Execute(name = "setMoveDelay")
  void setMoveDelay(@Context final CommandSource commandSource, @Arg final long moveDelay)
      throws IllegalAccessException {
    queueConfig.setDelay(moveDelay);
    flameConfigService.save(QueueConfig.class);
    flameConfigService.refreshAndBroadcast(QueueConfig.class);
    VelocityMessage.from("&aUstawiono opóźnienie przeniesienia na: &2" + moveDelay + " ms")
        .deliver(commandSource);
  }

  @Execute(name = "status")
  void exec(@Context final CommandSource commandSource) {

    commandSource.sendMessage(TextUtil.parse("&7Lista kolejek:"));
    for (final Queue queue : queueService.getQueues()) {
      final int size = queue.getEntries().size();
      commandSource.sendMessage(
          TextUtil.parse("&8- &a" + queue.getName() + " &7(&f" + size + "os&7)"));
    }

  }

  @Execute(name = "join")
  void exec(@Context final Player player, @Arg final String queueName) {

    final List<NetworkServer> serversByCategory = networkServerFacade.findServersByCategory(
        queueName);
    if (serversByCategory == null || serversByCategory.isEmpty()) {
      player.sendMessage(TextUtil.parse("&cBrak serwerów."));
      return;
    }

    final Queue queue = queueService.getOrCreate(queueName);
    queue.addEntry(player.getUsername());

    player.createConnectionRequest(
            proxyServer.getServer("queue").get()
        )
        .fireAndForget();
    player.sendMessage(TextUtil.parse("&aDołączono do kolejki: &2" + queue));

  }

  @Execute(name = "instantMoveSafe")
  public void instantMoveSafe(@Context final CommandSource source, @Arg final String queueName) {
    final List<NetworkServer> serversByCategory = networkServerFacade.findServersByCategory(
        queueName);
    if (serversByCategory == null || serversByCategory.isEmpty()) {
      source.sendMessage(TextUtil.parse("&cBrak serwerów."));
      return;
    }

    final Queue queue = queueService.getOrCreate(queueName);
    int i = 0;
    for (final String entry : queue.getEntries()) {
      if (queueRedirectService.move(entry, queue)) {
        i++;
      }
    }

    source.sendMessage(
        TextUtil.parse("&aPomyślnie przeniesiono " + i + " graczy z kolejki " + queue));

  }


}
