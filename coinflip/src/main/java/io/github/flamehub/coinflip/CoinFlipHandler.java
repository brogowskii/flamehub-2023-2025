package io.github.flamehub.coinflip;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CoinFlipHandler {

  private final FlameDispatcher flameDispatcher;

  public CoinFlipHandler(final FlameDispatcher flameDispatcher) {
    this.flameDispatcher = flameDispatcher;
  }

  @PacketHandler
  void handle(final CoinFlipSpinPacket packet) {
    final UUID playerUniqueId = packet.getPlayerUniqueId();
    final Player player = Bukkit.getPlayer(playerUniqueId);
    if (player == null) {
      return;
    }

    final CoinFlipSpinGui coinFlipSpinGui = new CoinFlipSpinGui(
        ItemStack.deserializeBytes(packet.getWinnerHead()),
        ItemStack.deserializeBytes(packet.getLoserHead()),
        itemStack -> {});

    flameDispatcher.dispatch(() -> coinFlipSpinGui.spin(player));

  }

}
