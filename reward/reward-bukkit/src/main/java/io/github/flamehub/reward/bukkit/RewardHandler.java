package io.github.flamehub.reward.bukkit;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.util.HexUtil;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.reward.api.RewardReceivedPacket;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;

public final class RewardHandler {

  private final NetworkMessageService networkMessageService;
  private final FlameDispatcher flameDispatcher;
  private final RewardConfig config;

  public RewardHandler(
      final NetworkMessageService networkMessageService,
      final FlameDispatcher flameDispatcher,
      final RewardConfig config
  ) {
    this.networkMessageService = networkMessageService;
    this.flameDispatcher = flameDispatcher;
    this.config = config;
  }

  @PacketHandler
  public void handle(final RewardReceivedPacket packet) {

    final List<String> build = BukkitMessage.from(
            "",
            "&#319EC5\uD83C\uDFA3 &8| &#319EC5&l/&#37A4CB&lɴ&#3EAAD1&lᴀ&#44AFD6&lɢ&#4AB5DC&lʀ&#42ADD4&lᴏ&#39A6CD&lᴅ&#319EC5&lᴀ &8▶ &fGracz &f{player} &fodebrał nagrodę",
            "&fza &#4AB5DCdołączenie &fna naszego &#4AB5DC&ndiscorda serwerowego&f!",
            "&fLink do discorda: &f{discord}",
            ""
        )
        .with("player",
            HexUtil.interpolateColors(packet.getPlayerName(), "#319EC5", "#4AB5DC", false))
        .with("discord",
            HexUtil.interpolateColors("https://dc.flamehub.pl/", "#319EC5", "#4AB5DC", false))
        .apply();

    networkMessageService.send(
        build,
        NetworkMessageFilter.builder()
            .idForHide("rewards")
            .build(),
        NetworkMessageType.CHAT
    );

    flameDispatcher.dispatch(() -> {
      final Server server = Bukkit.getServer();
      final ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
      for (final String command : config.getCommand()) {
        server.dispatchCommand(consoleSender,
            command.replace("{PLAYER}", packet.getPlayerName()));
      }
    });


  }
}
