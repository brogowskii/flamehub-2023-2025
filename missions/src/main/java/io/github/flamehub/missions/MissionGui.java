package io.github.flamehub.missions;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.missions.user.MissionUser;
import io.github.flamehub.missions.user.MissionUserCache;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public final class MissionGui {

  private final MissionUserCache missionUserCache;
  private final MissionConfig missionConfig;

  public MissionGui(final MissionUserCache missionUserCache, final MissionConfig missionConfig) {
    this.missionUserCache = missionUserCache;
    this.missionConfig = missionConfig;
  }

  public void open(final Player player) {
    final MissionUser missionUser = missionUserCache.findByKey(player.getUniqueId());
    resetCompletedMissionsIfNeeded(missionUser);

    final Gui gui = Gui.gui()
        .rows(5)
        .title(TextUtil.parse("&#9863E7\uD83E\uDDEA &8| &#9863E7&lᴍɪꜱᴊᴇ"))
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui5(gui);

    final List<MissionProgress> activeMissions = missionUser.getActiveMissions();

    for (int i = 0; i < activeMissions.size(); i++) {
      final MissionProgress mission = activeMissions.get(i);

      final FlameItemBuilder builder = FlameItemBuilder.of(mission.getType().getIcon())
          .glow()
          .flag(ItemFlag.HIDE_ATTRIBUTES)
          .name("&#9863E7&lᴍɪꜱᴊᴀ #" + (i + 1))
          .lore(
              "",
              " &8▶ &7Misja: &f" + mission.getType().getDescription(),
              " &8▶ &7Postęp: &a" + mission.getProgress() + "&8/&7" + mission.getRequired()
          );

      if (mission.getProgress() < mission.getRequired()) {
        builder.appendLore(
            "",
            " &8▶ &fNagroda: &7" + mission.getExperience() + " &x&E&C&7&2&7&2&lꜱ&x&E&A&6&7&6&7&lᴍ&x&E&7&5&C&5&C&lᴏ&x&E&5&5&0&5&0&lᴄ&x&E&2&4&5&4&5&lᴢ&x&E&0&3&A&3&A&lʏ &x&E&6&5&6&5&6&lᴘ&x&E&9&6&4&6&4&lʏ&x&E&C&7&2&7&2&lʟ",
            "",
            "&cNie możesz jeszcze odebrać nagrody!"
        );
      } else if (!mission.isClaimed()) {
        builder.appendLore(
            "",
            " &8▶ &fNagroda: &7" + mission.getExperience() + " &x&E&C&7&2&7&2&lꜱ&x&E&A&6&7&6&7&lᴍ&x&E&7&5&C&5&C&lᴏ&x&E&5&5&0&5&0&lᴄ&x&E&2&4&5&4&5&lᴢ&x&E&0&3&A&3&A&lʏ &x&E&6&5&6&5&6&lᴘ&x&E&9&6&4&6&4&lʏ&x&E&C&7&2&7&2&lʟ",
            "",
            "&aKliknij, aby odebrać nagrodę!"
        );
      } else {
        builder.appendLore(
            "",
            "&aNagroda została odebrana!",
            ""
        );
      }

      gui.setItem(3, i + 4, builder.asGuiItem(event -> {
        if (mission.isClaimed()) {
          BukkitMessage.from("&cJuż odebrałeś nagrodę za tę misję.").deliver(player);
          return;
        }

        if (mission.getProgress() < mission.getRequired()) {
          BukkitMessage.from("&cNie spełniasz wymagań do odebrania nagrody.").deliver(player);
          return;
        }

        mission.setClaimed(true);
        missionUser.markToUpdate();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "upgradeadmin givecurrency " + player.getName() + " " + mission.getExperience());
        BukkitMessage.from("&aPomyślnie odebrano nagrodę!").deliver(player);
        open(player);
      }));
    }

    gui.open(player);
  }

  private void resetCompletedMissionsIfNeeded(final MissionUser missionUser) {
    final long currentTime = System.currentTimeMillis();
    final LocalDateTime now = LocalDateTime.now();
    final LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
    final long midnightMillis = midnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    final List<MissionProgress> activeMissions = missionUser.getActiveMissions();

    // If the mission list is empty, initialize it with 3 unique missions
    if (activeMissions.isEmpty()) {
      activeMissions.addAll(generateUniqueMissions(3));
      missionUser.markToUpdate();
      return;
    }

    // Reset completed missions after midnight
    if (currentTime >= midnightMillis) {
      for (int i = 0; i < activeMissions.size(); i++) {
        final MissionProgress mission = activeMissions.get(i);
        if (mission.isClaimed()) {
          activeMissions.set(i, generateNewMission(activeMissions));
        }
      }
      missionUser.markToUpdate();
    }
  }

  private List<MissionProgress> generateUniqueMissions(final int count) {
    final List<MissionConfig.MissionDefinition> missionDefinitions = missionConfig.getMissions();
    final List<MissionProgress> uniqueMissions = new ArrayList<>();
    final Set<MissionType> usedTypes = new HashSet<>();

    while (uniqueMissions.size() < count) {
      final MissionConfig.MissionDefinition selectedDefinition = missionDefinitions.get(
          ThreadLocalRandom.current().nextInt(missionDefinitions.size())
      );
      if (selectedDefinition.getType() == MissionType.PUMPKIN_BREAK) {
        continue;
      }

      if (!usedTypes.contains(selectedDefinition.getType())) {
        usedTypes.add(selectedDefinition.getType());
        uniqueMissions.add(new MissionProgress(
            selectedDefinition.getType(),
            selectedDefinition.getRequired(),
            selectedDefinition.getExperience()
        ));
      }
    }

    return uniqueMissions;
  }

  private MissionProgress generateNewMission(final List<MissionProgress> existingMissions) {
    final List<MissionConfig.MissionDefinition> missionDefinitions = missionConfig.getMissions();
    final Set<MissionType> usedTypes = existingMissions.stream()
        .map(MissionProgress::getType)
        .collect(Collectors.toSet());

    MissionConfig.MissionDefinition selectedDefinition;
    do {
      selectedDefinition = missionDefinitions.get(
          ThreadLocalRandom.current().nextInt(missionDefinitions.size())
      );
    } while (usedTypes.contains(selectedDefinition.getType()));

    return new MissionProgress(
        selectedDefinition.getType(),
        selectedDefinition.getRequired(),
        selectedDefinition.getExperience()
    );
  }
}