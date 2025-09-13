package io.github.flamehub.cheque;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class ChequeListener implements Listener {

  private final ChequeService chequeService;
  private final ChequeLogRepository chequeLogRepository;

  public ChequeListener(final ChequeService chequeService,
      final ChequeLogRepository chequeLogRepository) {
    this.chequeService = chequeService;
    this.chequeLogRepository = chequeLogRepository;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInteract(final PlayerInteractEvent event) {

    if (event.getHand() != EquipmentSlot.HAND) {
      return;
    }

    if (event.getAction() == Action.RIGHT_CLICK_AIR
        || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

      final Player player = event.getPlayer();
      final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
      if (itemInMainHand.getType() == Material.PAPER) {
        final double money = chequeService.useCheque(player, itemInMainHand);
        if (money > 0) {
          CompletableFuture.runAsync(() -> {
            final ChequeLog chequeLog = ChequeLog.builder()
                .type(ChequeLogType.USE)
                .who(player.getName())
                .money(money)
                .build();
            chequeLogRepository.save(chequeLog);
            chequeService.sendWebhook(chequeLog);
          });
        }
        event.setCancelled(true);
      }

    }

  }

}
