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
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.proxy.core.ProxyCore;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.util.TextUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Command(name = "queue")
@Permission("server.velocity.commands.queue")
public final class QueueCommand {

  private final ProxyServer proxyServer;
  private final FlameConfigService flameConfigService;
  private final QueueConfig queueConfig;
  private final QueueService queueService;
  private final NetworkServerCache networkServerCache;
  private final QueueRedirectService queueRedirectService;

  public QueueCommand(final ProxyServer proxyServer, final FlameConfigService flameConfigService,
      final QueueConfig queueConfig, final QueueService queueService,
      final NetworkServerCache networkServerCache, final QueueRedirectService queueRedirectService) {
    this.proxyServer = proxyServer;
    this.flameConfigService = flameConfigService;
    this.queueConfig = queueConfig;
    this.queueService = queueService;
    this.networkServerCache = networkServerCache;
    this.queueRedirectService = queueRedirectService;
  }

  @Execute(name = "setPlayersPerMove")
  void setPlayersPerMove(@Context CommandSource commandSource, @Arg int playersPerMove)
      throws IllegalAccessException {
    this.queueConfig.setPlayersPerMove(playersPerMove);
    this.flameConfigService.saveLocally(QueueConfig.class);
    this.flameConfigService.update(QueueConfig.class);
    VelocityMessage.from("&aUstawiono ilość graczy na przeniesienie: &2" + playersPerMove)
        .send(commandSource);
  }

  @Execute(name = "setMoveDelay")
  void setMoveDelay(@Context CommandSource commandSource, @Arg long moveDelay) throws IllegalAccessException {
    this.queueConfig.setDelay(moveDelay);
    this.flameConfigService.saveLocally(QueueConfig.class);
    this.flameConfigService.update(QueueConfig.class);
    VelocityMessage.from("&aUstawiono opóźnienie przeniesienia na: &2" + moveDelay + " ms").send(commandSource);
  }

  @Execute(name = "status")
  void exec(@Context CommandSource commandSource) {

    commandSource.sendMessage(TextUtil.parse("&7Lista kolejek:"));
    for (Queue queue : this.queueService.getQueues()) {
      int size = queue.getEntries().size();
      commandSource.sendMessage(
          TextUtil.parse("&8- &a" + queue.getName() + " &7(&f" + size + "os&7)"));
    }

  }

  @Execute(name = "join")
  void exec(@Context Player player, @Arg String queueName) {

    List<NetworkServer> serversByCategory = this.networkServerCache.findServersByCategory(queueName);
    if (serversByCategory == null || serversByCategory.isEmpty()) {
      player.sendMessage(TextUtil.parse("&cBrak serwerów."));
      return;
    }

    final Queue queue = this.queueService.getOrCreate(queueName);
    queue.addEntry(player.getUsername());

    player.createConnectionRequest(
            ProxyCore
                .getInstance()
                .getProxyServer()
                .getServer("queue")
                .get()
        )
        .fireAndForget();
    player.sendMessage(TextUtil.parse("&aDołączono do kolejki: &2" + queue));

  }

  @Execute(name = "instantMoveSafe")
  public void instantMoveSafe(@Context CommandSource source, @Arg String queueName) {
    List<NetworkServer> serversByCategory = this.networkServerCache.findServersByCategory(queueName);
    if (serversByCategory == null || serversByCategory.isEmpty()) {
      source.sendMessage(TextUtil.parse("&cBrak serwerów."));
      return;
    }

    final Queue queue = this.queueService.getOrCreate(queueName);
    int i = 0;
    for (String entry : queue.getEntries()) {
      if (this.queueRedirectService.move(entry, queue)) {
        i++;
      }
    }

    source.sendMessage(
        TextUtil.parse("&aPomyślnie przeniesiono " + i + " graczy z kolejki " + queue));

  }


}
