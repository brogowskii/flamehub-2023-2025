package io.github.flamehub.achievements.achievement;

import io.github.flamehub.achievements.achievement.user.AchievementUser;
import io.github.flamehub.achievements.achievement.user.AchievementUserCache;
import io.github.flamehub.crates.CrateOpenEvent;
import io.github.flamehub.timeplayed.user.SpendTimeIncrementEvent;
import io.github.flamehub.timeplayed.user.TimePlayedUser;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public final class AchievementListener implements Listener {

  private final AchievementService achievementService;
  private final AchievementUserCache achievementUserCache;

  public AchievementListener(final AchievementService achievementService,
      final AchievementUserCache achievementUserCache) {
    this.achievementService = achievementService;
    this.achievementUserCache = achievementUserCache;
  }


  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onBreak(final BlockBreakEvent event) {
    if (event.isCancelled()) {
      return;
    }

    final Material type = event.getBlock().getType();
    final Player player = event.getPlayer();
    final AchievementUser user = achievementUserCache.findByUniqueId(player.getUniqueId());
    final List<AchievementCategory> categoriesByAction = achievementService.getAchievementsCategoryByAction(
        AchievementActionType.BLOCK_BREAK);

    for (final AchievementCategory category : categoriesByAction) {
      final AchievementAction action = category.getAction();
      final List<Material> material = action.getMaterial();

      if (material == null || material.contains(type)) {
        user.addAchievementProgress(category.getId(), 1);
        user.setNeedUpdate(true);
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onPlace(final BlockPlaceEvent event) {
    if (event.isCancelled()) {
      return;
    }

    final Material type = event.getBlock().getType();
    final Player player = event.getPlayer();
    final AchievementUser user = achievementUserCache.findByUniqueId(player.getUniqueId());
    final List<AchievementCategory> categoriesByAction = achievementService.getAchievementsCategoryByAction(
        AchievementActionType.BLOCK_PLACE);

    for (final AchievementCategory category : categoriesByAction) {
      final AchievementAction action = category.getAction();
      final List<Material> material = action.getMaterial();

      if (material == null || material.contains(type)) {
        user.addAchievementProgress(category.getId(), 1);
        user.setNeedUpdate(true);
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onEat(final PlayerItemConsumeEvent event) {
    if (event.isCancelled()) {
      return;
    }

    final Player player = event.getPlayer();
    final AchievementUser user = achievementUserCache.findByUniqueId(player.getUniqueId());
    final Material type = event.getItem().getType();
    final List<AchievementCategory> categoriesByAction = achievementService.getAchievementsCategoryByAction(
        AchievementActionType.EAT);

    for (final AchievementCategory category : categoriesByAction) {
      final AchievementAction action = category.getAction();
      final List<Material> material = action.getMaterial();

      if (material == null || material.contains(type)) {
        user.addAchievementProgress(category.getId(), 1);
        user.setNeedUpdate(true);
      }
    }
  }

  @EventHandler
  public void onTimeIncrement(final SpendTimeIncrementEvent event) {
    final TimePlayedUser user = event.getUser();
    final AchievementUser achievementUser = achievementUserCache.findByUniqueId(user.getUniqueId());
    if (achievementUser == null) {
      return;
    }

    achievementUser.setAchievementProgress("spend_time", user.getSpendTime());
    achievementUser.setNeedUpdate(true);
  }

  @EventHandler
  public void onCrateOpen(final CrateOpenEvent event) {
    final Player player = event.getPlayer();
    final AchievementUser user = achievementUserCache.findByUniqueId(player.getUniqueId());
    if (user == null) {
      return;
    }

    user.addAchievementProgress("open_crate", 1);
    user.setNeedUpdate(true);
  }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    void onEntityDamage(EntityDamageByEntityEvent event) {
//        Entity entity = event.getEntity();
//        if (entity instanceof Player player) {
//            if (player.getHealth() - event.getFinalDamage() <= 0) {
//                var totem = player.getInventory().getItemInMainHand();
//
//                if (totem.getType().isAir()) {
//                    totem = player.getInventory().getItemInOffHand();
//                }
//
//                if (totem.getType() == Material.TOTEM_OF_UNDYING) {
//                    if (entity.getdamager())
//                }
//            }
//        }
//    }
}