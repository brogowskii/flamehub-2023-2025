package io.github.flamehub.missions;

import io.github.flamehub.commons.bukkit.user.event.AsyncPlayerJoinEvent;
import io.github.flamehub.missions.user.MissionUser;
import io.github.flamehub.missions.user.MissionUserCache;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public final class MissionListener implements Listener {

  private final MissionUserCache missionUserCache;

  public MissionListener(MissionUserCache missionUserCache) {
    this.missionUserCache = missionUserCache;
  }

  @EventHandler
  public void onUserJoin(AsyncPlayerJoinEvent event) {

    if (event.getUser() instanceof MissionUser missionUser) {

      final Mission dailyMission = missionUser.getDailyMission();
      if (dailyMission == null || dailyMission.getExpiration() < System.currentTimeMillis()) {

        final MissionType missionType = MissionType.values()[(int) (Math.random()
            * MissionType.values().length)];
        final Mission mission = new Mission(
            missionType,
            missionType.getRequired()[(int) (Math.random() * missionType.getRequired().length)],
            ThreadLocalRandom.current().nextInt(16, 32)
        );

        missionUser.setDailyMission(mission);
        missionUser.markToUpdate();
      }

    }

  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onBreak(BlockBreakEvent event) {

    Player player = event.getPlayer();
    MissionUser missionUser = missionUserCache.findByKey(player.getUniqueId());
    Mission dailyMission = missionUser.getDailyMission();
    if (dailyMission.getType() == MissionType.BLOCK_BREAK) {
      dailyMission.setProgress(dailyMission.getProgress() + 1);
      missionUser.markToUpdate();
    }

  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onDamage(EntityDamageByEntityEvent event) {

    if (event.getDamager() instanceof Player player) {

      MissionUser missionUser = missionUserCache.findByKey(player.getUniqueId());
      Mission dailyMission = missionUser.getDailyMission();
      if (dailyMission.getType() == MissionType.DAMAGE_DEALT) {
        dailyMission.setProgress(dailyMission.getProgress() + (int) event.getFinalDamage());
        missionUser.markToUpdate();
      }

    }

  }

  @EventHandler
  public void onEat(PlayerItemConsumeEvent event) {

    ItemStack item = event.getItem();
    if (item.getType() != Material.GOLDEN_APPLE) {
      return;
    }

    Player player = event.getPlayer();
    MissionUser missionUser = missionUserCache.findByKey(player.getUniqueId());
    Mission dailyMission = missionUser.getDailyMission();

    if (dailyMission.getType() == MissionType.EAT_GOLDEN_APPLES) {
      dailyMission.setProgress(dailyMission.getProgress() + 1);
      missionUser.markToUpdate();
    }


  }

}
