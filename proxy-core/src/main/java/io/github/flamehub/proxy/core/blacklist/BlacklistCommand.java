package io.github.flamehub.proxy.core.blacklist;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.RootCommand;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.proxy.core.ProxyMessages;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import java.time.Instant;
import java.util.Optional;

@RootCommand
public final class BlacklistCommand {

  private final ProxyServer proxyServer;
  private final BlacklistRepository blacklistRepository;
  private final ProxyMessages proxyMessages;

  public BlacklistCommand(
      final ProxyServer proxyServer,
      final BlacklistRepository blacklistRepository,
      final ProxyMessages proxyMessages) {
    this.proxyServer = proxyServer;
    this.blacklistRepository = blacklistRepository;
    this.proxyMessages = proxyMessages;
  }

  @Execute(name = "blacklist")
  @Permission("server.commands.blacklist")
  void blacklist(@Context final CommandSource source, @Arg final String networkPlayer,
      @Join final String reason) {

    final String admin = source instanceof Player ? ((Player) source).getUsername() : "Console";
    Blacklist blacklist = blacklistRepository.load("nickname", networkPlayer);
    if (blacklist == null) {
      blacklist = new Blacklist(networkPlayer, reason, admin, Instant.now());
    } else {
      blacklist.setReason(reason);
      blacklist.setAdmin(admin);
      blacklist.setDate(Instant.now());
    }

    blacklistRepository.save(blacklist);
    VelocityMessage.from("&cGracz &4" + networkPlayer + " &czostał dodany do czarnej listy!")
        .deliver(source);

    final Optional<Player> optionalPlayer = proxyServer.getPlayer(networkPlayer);
    if (optionalPlayer.isPresent()) {
      final Player player = optionalPlayer.get();
      player.disconnect(proxyMessages.blacklistKick
          .with("reason", reason)
          .with("admin", admin)
          .with("date", TimeUtil.formatDate(blacklist.getDate()))
          .applyFirstAsComponent());
    }


  }

  @Execute(name = "unblacklist", aliases = "unbl")
  @Permission("server.commands.unblacklist")
  void unBlacklist(@Context final CommandSource source, @Arg final String networkPlayer) {

    final Blacklist blacklist = blacklistRepository.load("nickname", networkPlayer);
    if (blacklist == null) {
      VelocityMessage.from("&cGracz nie jest na czarnej liście!").deliver(source);
      return;
    }

    blacklistRepository.delete(blacklist);
    VelocityMessage.from("&aGracz &2" + networkPlayer + " &azostał usunięty z czarnej listy!")
        .deliver(source);
  }

}
