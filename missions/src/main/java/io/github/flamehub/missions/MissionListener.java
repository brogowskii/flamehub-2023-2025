package io.github.flamehub.missions;

import io.github.flamehub.commons.bukkit.user.event.AsyncPlayerJoinEvent;
import io.github.flamehub.crates.CrateOpenEvent;
import io.github.flamehub.missions.user.MissionUser;
import io.github.flamehub.missions.user.MissionUserCache;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public final class MissionListener implements Listener {

  private final MissionUserCache missionUserCache;

  public MissionListener(final MissionUserCache missionUserCache) {
    this.missionUserCache = missionUserCache;
  }

  @EventHandler
  public void onJoin(final AsyncPlayerJoinEvent event) {
    if (event.getUser() instanceof final MissionUser missionUser) {
      missionUser.getActiveMissions()
          .removeIf(missionProgress -> missionProgress.getType() == MissionType.PUMPKIN_BREAK);
      missionUser.setNeedUpdate(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(final BlockBreakEvent event) {
    final Player player = event.getPlayer();
    final MissionUser missionUser = missionUserCache.findByKey(player.getUniqueId());

    missionUser.getActiveMissions().forEach(mission -> {
      if (mission.getType() == MissionType.BLOCK_BREAK) {
        mission.setProgress(mission.getProgress() + 1);
        missionUser.markToUpdate();
      }

      if (mission.getType() == MissionType.WOOL_BREAK) {
        if (event.getBlock().getType().toString().contains("WOOL")) {
          mission.setProgress(mission.getProgress() + 1);
          missionUser.markToUpdate();
        }
      }
    });
  }

  @EventHandler
  public void onDamage(final EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof final Player player) {
      final MissionUser missionUser = missionUserCache.findByKey(player.getUniqueId());

      missionUser.getActiveMissions().forEach(mission -> {
        if (mission.getType() == MissionType.DAMAGE_DEALT) {
          mission.setProgress(mission.getProgress() + (event.isCritical() ? 2 : 1));
          missionUser.markToUpdate();
        }
      });
    }
  }

  @EventHandler
  public void onEat(final PlayerItemConsumeEvent event) {
    final ItemStack item = event.getItem();
    if (item.getType() != Material.GOLDEN_APPLE) {
      return;
    }

    final Player player = event.getPlayer();
    final MissionUser missionUser = missionUserCache.findByKey(player.getUniqueId());

    missionUser.getActiveMissions().forEach(mission -> {
      if (mission.getType() == MissionType.EAT_GOLDEN_APPLES) {
        mission.setProgress(mission.getProgress() + 1);
        missionUser.markToUpdate();
      }
    });
  }

  @EventHandler
  public void onCrateOpen(final CrateOpenEvent event) {
    final Player player = event.getPlayer();
    final MissionUser missionUser = missionUserCache.findByKey(player.getUniqueId());

    missionUser.getActiveMissions().forEach(mission -> {
      if (mission.getType() == MissionType.OPEN_CRATE) {
        mission.setProgress(mission.getProgress() + 1);
        missionUser.markToUpdate();
      }
    });
  }
}