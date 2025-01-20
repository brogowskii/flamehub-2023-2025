package io.github.flamehub.missions;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.missions.user.MissionUser;
import io.github.flamehub.missions.user.MissionUserCache;
import io.github.flamehub.missions.user.MissionUserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public final class MissionGui {

  private final MissionUserCache missionUserCache;
  private final MissionUserRepository missionUserRepository;

  public MissionGui(MissionUserCache missionUserCache,
      MissionUserRepository missionUserRepository) {
    this.missionUserCache = missionUserCache;
    this.missionUserRepository = missionUserRepository;
  }

  void open(Player player) {

    Gui gui = Gui.gui()
        .rows(5)
        .title(TextUtil.parse(
            "&#9863E7\uD83E\uDDEA &8| &#9863E7&lᴍ&#9863E7&lɪ&#9863E7&lꜱ&#9863E7&lᴊ&#9863E7&lᴇ"))
        .disableAllInteractions()
        .disableOtherActions()
        .create();

    GuiHelper.fillGui5(gui);

    MissionUser missionUser = missionUserCache.findByKey(player.getUniqueId());
    Mission dailyMission = missionUser.getDailyMission();

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

    FlameItemBuilder builder = FlameItemBuilder.of(dailyMission.getType().getIcon())
        .glow()
        .flag(ItemFlag.HIDE_ATTRIBUTES)
        .flag(ItemFlag.HIDE_ITEM_SPECIFICS)
        .flag(ItemFlag.HIDE_DESTROYS)
        .name("&#9863E7&lᴅᴢɪsɪᴇᴊsᴢᴀ ᴍɪsᴊᴀ")
        .lore(
            "",
            " &8▶ &7Misja: &f" + dailyMission.getType().getDescription(),
            " &8▶ &7Postęp: &a" + dailyMission.getProgress() + "&8/&7" + dailyMission.getRequired(),
            " &8▶ &7Wygaśnie za: &c" + TimeUtil.formatTime(
                Duration.between(Instant.now(), Instant.ofEpochMilli(dailyMission.getExpiration())))
        );

    if (dailyMission.getProgress() < dailyMission.getRequired()) {
      builder.appendLore(
          "",
          " &8▶ &7Nagroda za ukończenie:",
          " &f&lx" + dailyMission.getShards()
              + " &x&2&D&C&A&D&E&lᴏ&x&4&7&D&3&E&5&lᴅ&x&6&1&D&C&E&D&lʏ&x&7&A&E&5&F&4&lꜱ&x&9&4&E&E&F&B&lꜱ&x&7&2&E&2&F&1&lɪ&x&4&F&D&6&E&8&lᴜ&x&2&D&C&A&D&E&lᴍ",
          "",
          "&cNie możesz jeszcze odebrać nagrody!"
      );
    } else if (!dailyMission.isClaimed()) {
      builder.appendLore(
          "",
          " &8▶ &7Nagroda za ukończenie:",
          " &f&lx" + dailyMission.getShards()
              + " &x&2&D&C&A&D&E&lᴏ&x&4&7&D&3&E&5&lᴅ&x&6&1&D&C&E&D&lʏ&x&7&A&E&5&F&4&lꜱ&x&9&4&E&E&F&B&lꜱ&x&7&2&E&2&F&1&lɪ&x&4&F&D&6&E&8&lᴜ&x&2&D&C&A&D&E&lᴍ",
          "",
          "&aKliknij tutaj, aby odebrać nagrodę za tą misję."
      );
    } else {

      builder.appendLore(
          "",
          " &8▶ &aMisja została ukończona!",
          " &8▶ &aNastępna misja zostanie wylosowana za: ",
          " &8▶ &2" + TimeUtil.formatTime(
              Duration.between(Instant.now(), Instant.ofEpochMilli(dailyMission.getExpiration()))),
          ""
      );

    }

    gui.setItem(3, 5, builder.asGuiItem(event -> {

      if (dailyMission.isClaimed()) {
        BukkitMessage.from("&cJuż odebrałeś nagrodę za tą misję.").deliver(player);
        return;
      }

      if (dailyMission.getProgress() < dailyMission.getRequired()) {
        BukkitMessage.from("&cNie spełniasz wymagań do odebrania nagrody za tą misję.")
            .deliver(player);
        return;
      }

      dailyMission.setClaimed(true);
      missionUser.markToUpdate();
      open(player);

      Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
          "upgradeadmin givecurrency " + player.getName() + " " + dailyMission.getShards());
      BukkitMessage.from("&aPomyślnie odebrano nagrodę!").deliver(player);
    }));

    gui.open(player);

  }

}
