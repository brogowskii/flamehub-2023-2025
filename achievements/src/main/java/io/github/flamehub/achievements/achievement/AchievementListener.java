package io.github.flamehub.achievements.achievement;

import io.github.flamehub.achievements.achievement.user.AchievementUser;
import io.github.flamehub.achievements.achievement.user.AchievementUserCache;
import io.github.flamehub.crates.crate.CrateOpenEvent;
import io.github.flamehub.timeplayed.user.SpendTimeIncrementEvent;
import io.github.flamehub.timeplayed.user.TimePlayedUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.List;

public class AchievementListener implements Listener {

    private final AchievementService achievementService;
    private final AchievementUserCache achievementUserCache;

    public AchievementListener(AchievementService achievementService, AchievementUserCache achievementUserCache) {
        this.achievementService = achievementService;
        this.achievementUserCache = achievementUserCache;
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Material type = event.getBlock().getType();
        Player player = event.getPlayer();
        AchievementUser user = this.achievementUserCache.findByUniqueId(player.getUniqueId());
        List<AchievementCategory> categoriesByAction = this.achievementService.getAchievementsCategoryByAction(AchievementActionType.BLOCK_BREAK);

        for (AchievementCategory category : categoriesByAction) {
            AchievementAction action = category.getAction();
            Material material = action.getMaterial();

            if (material == null || material.equals(type)) {
                user.addAchievementProgress(category.getId(), 1);
                user.setNeedUpdate(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Material type = event.getBlock().getType();
        Player player = event.getPlayer();
        AchievementUser user = this.achievementUserCache.findByUniqueId(player.getUniqueId());
        List<AchievementCategory> categoriesByAction = this.achievementService.getAchievementsCategoryByAction(AchievementActionType.BLOCK_PLACE);

        for (AchievementCategory category : categoriesByAction) {
            AchievementAction action = category.getAction();
            Material material = action.getMaterial();

            if (material == null || material.equals(type)) {
                user.addAchievementProgress(category.getId(), 1);
                user.setNeedUpdate(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEat(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        AchievementUser user = this.achievementUserCache.findByUniqueId(player.getUniqueId());
        Material type = event.getItem().getType();
        List<AchievementCategory> categoriesByAction = this.achievementService.getAchievementsCategoryByAction(AchievementActionType.EAT);

        for (AchievementCategory category : categoriesByAction) {
            AchievementAction action = category.getAction();
            Material material = action.getMaterial();

            if (material == null || material.equals(type)) {
                user.addAchievementProgress(category.getId(), 1);
                user.setNeedUpdate(true);
            }
        }
    }

    @EventHandler
    public void onTimeIncrement(SpendTimeIncrementEvent event) {
        TimePlayedUser user = event.getUser();
        AchievementUser achievementUser = this.achievementUserCache.findByUniqueId(user.getUniqueId());
        if (achievementUser == null) {
            return;
        }

        achievementUser.setAchievementProgress("spend_time", user.getSpendTime());
        achievementUser.setNeedUpdate(true);
    }

    @EventHandler
    public void onCrateOpen(CrateOpenEvent event) {
        Player player = event.getPlayer();
        AchievementUser user = this.achievementUserCache.findByUniqueId(player.getUniqueId());
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