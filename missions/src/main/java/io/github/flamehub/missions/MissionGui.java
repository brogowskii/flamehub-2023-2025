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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public final class MissionGui {

    private final MissionUserCache missionUserCache;
    private final MissionUserRepository missionUserRepository;

    public MissionGui(MissionUserCache missionUserCache, MissionUserRepository missionUserRepository) {
        this.missionUserCache = missionUserCache;
        this.missionUserRepository = missionUserRepository;
    }

    void open(Player player) {

        Gui gui = Gui.gui()
                .rows(5)
                .title(TextUtil.parse("&#9863E7\uD83E\uDDEA &8| &#9863E7&lᴍ&#9863E7&lɪ&#9863E7&lꜱ&#9863E7&lᴊ&#9863E7&lᴇ"))
                .disableAllInteractions()
                .disableOtherActions()
                .create();

        GuiHelper.fillGui5(gui);

        MissionUser missionUser = this.missionUserCache.findByKey(player.getUniqueId());
        Mission dailyMission = missionUser.getDailyMission();

        if (dailyMission == null || dailyMission.getExpiration() < System.currentTimeMillis()) {

            final MissionType missionType = MissionType.values()[(int) (Math.random() * MissionType.values().length)];
            final Mission mission = new Mission(
                    missionType,
                    missionType.getRequired()[(int) (Math.random() * missionType.getRequired().length)],
                    ThreadLocalRandom.current().nextInt(24, 48)
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
                        " &8▶ &7Postęp: &a" + dailyMission.getProgress() + "&8/&7" + dailyMission.getRequired()
                );

        if (dailyMission.getProgress() < dailyMission.getRequired()) {
            builder.appendLore(
                    "",
                    " &8▶ &7Nagroda za ukończenie:",
                    " &f&lx" + dailyMission.getShards() + " &x&E&8&B&2&F&B&lғ&x&E&9&A&D&F&8&lʀ&x&E&A&A&9&F&5&lᴀ&x&E&C&A&4&F&1&lɢ&x&E&D&9&F&E&E&lᴍ&x&E&E&9&A&E&B&lᴇ&x&E&F&9&6&E&8&lɴ&x&F&0&9&1&E&5&lᴛ &x&F&2&8&C&E&2&lᴋ&x&F&3&8&7&D&E&lʀ&x&F&4&8&3&D&B&lʏ&x&F&5&7&E&D&8&ls&x&F&6&7&9&D&5&lᴢ&x&F&7&7&4&D&2&lᴛᴀʟᴜ",
                    "",
                    "&cNie możesz jeszcze odebrać nagrody!"
            );
        } else if (!dailyMission.isClaimed()) {
            builder.appendLore(
                    "",
                    " &8▶ &7Nagroda za ukończenie:",
                    " &f&lx" + dailyMission.getShards() + " &x&E&8&B&2&F&B&lғ&x&E&9&A&D&F&8&lʀ&x&E&A&A&9&F&5&lᴀ&x&E&C&A&4&F&1&lɢ&x&E&D&9&F&E&E&lᴍ&x&E&E&9&A&E&B&lᴇ&x&E&F&9&6&E&8&lɴ&x&F&0&9&1&E&5&lᴛ &x&F&2&8&C&E&2&lᴋ&x&F&3&8&7&D&E&lʀ&x&F&4&8&3&D&B&lʏ&x&F&5&7&E&D&8&ls&x&F&6&7&9&D&5&lᴢ&x&F&7&7&4&D&2&lᴛᴀʟᴜ",
                    "",
                    "&aKliknij tutaj, aby odebrać nagrodę za tą misję."
            );
        }
        else {

            builder.appendLore(
                    "",
                    " &8▶ &aMisja została ukończona!",
                    " &8▶ &aNastępna misja zostanie wylosowana za: ",
                    " &8▶ &2" + TimeUtil.formatTime(Duration.between(Instant.now(), Instant.ofEpochMilli(dailyMission.getExpiration()))),
                    ""
            );

        }

        gui.setItem(3, 5, builder.asGuiItem(event -> {

            if (dailyMission.isClaimed()) {
                BukkitMessage.from("&cJuż odebrałeś nagrodę za tą misję.").send(player);
                return;
            }

            if (dailyMission.getProgress() < dailyMission.getRequired()) {
                BukkitMessage.from("&cNie spełniasz wymagań do odebrania nagrody za tą misję.").send(player);
                return;
            }

            dailyMission.setClaimed(true);
            missionUser.markToUpdate();
            open(player);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "upgradeadmin givecurrency " + player.getName() + " " + dailyMission.getShards());
            BukkitMessage.from("&aPomyślnie odebrano nagrodę!").send(player);
        }));


        gui.open(player);

    }

}
