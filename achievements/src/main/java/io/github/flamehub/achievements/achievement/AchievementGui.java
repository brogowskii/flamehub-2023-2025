package io.github.flamehub.achievements.achievement;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.achievements.achievement.user.AchievementUser;
import io.github.flamehub.achievements.achievement.user.AchievementUserCache;
import io.github.flamehub.achievements.achievement.user.AchievementUserRepository;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.network.message.NetworkMessageFilterBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class AchievementGui {

    private final Player player;

    private final AchievementConfig achievementConfig;
    private final AchievementService achievementService;
    private final AchievementUserCache achievementUserCache;
    private final AchievementUserRepository achievementUserRepository;
    private final NetworkMessageService networkMessageService;
    private final NetworkServerCache networkServerCache;

    public AchievementGui(Player player, AchievementConfig achievementConfig, AchievementService achievementService, AchievementUserCache achievementUserCache, AchievementUserRepository achievementUserRepository, NetworkMessageService networkMessageService, NetworkServerCache networkServerCache) {
        this.player = player;
        this.achievementConfig = achievementConfig;
        this.achievementService = achievementService;
        this.achievementUserCache = achievementUserCache;
        this.achievementUserRepository = achievementUserRepository;
        this.networkMessageService = networkMessageService;
        this.networkServerCache = networkServerCache;
    }

    public void openSelection() {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        Gui gui = Gui.gui()
                .title(TextUtil.parse("&8&lOsiągnięcia"))
                .rows(5)
                .disableAllInteractions()
                .create();
        GuiHelper.fillGui5(gui);

        Map<String, AchievementCategory> achievementCategories = achievementConfig.getAchievementCategories();
        AchievementUser user = this.achievementUserCache.findByUniqueId(player.getUniqueId());

        for (AchievementCategory value : achievementCategories.values()) {
            int size = achievementService.size(value.getId());
            long claimedAchievementsCount = user.claimedAchievementsCount(value);
            boolean availableToClaim = achievementService.availableToClaimFromCategory(value.getId(), user) > 0;

            List<String> availableToClaimInfo = availableToClaim ?
                    Arrays.asList(" &ePosiadasz możliwe do odebrania", " &6osiągnięcie &ew tej kategorii.") :
                    Arrays.asList(" &cNie posiadasz możliwych do odebrania", " &4osiągnięć &cw tej kategorii.");

            FlameItemBuilder builder = FlameItemBuilder.of(value.getIcon())
                    .name("&b&l" + value.getFriendlyName())
                    .lore("", " &7Odebrane osiągnięcia: &f" + claimedAchievementsCount + "&8/&7" + size, "");

            builder
                    .appendLore(availableToClaimInfo)
                    .appendLore("", "&eKliknij, aby &6przeglądać &eosiągnięcia w tej kategorii.");

            gui.setItem(value.getSlot(), builder.asGuiItem(event -> openAchievements(value, user)));
        }

        gui.open(player);
    }


    private void openAchievements(AchievementCategory category, AchievementUser achievementUser) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        PaginatedGui gui = Gui.paginated()
                .title(TextUtil.parse("&8&l" + category.getFriendlyName()))
                .rows(3)
                .pageSize(9)
                .disableAllInteractions()
                .create();
        border(gui);

        gui.setItem(3, 5, FlameItemBuilder.of(Material.RED_CONCRETE)
                .name("&c&lPowrót")
                .lore(
                        "",
                        " &7Kliknij, aby powrócić na poprzednią stronę.",
                        ""
                )
                .asGuiItem(event -> {
                    openSelection();
                }));

        achievementService.findByCategories(category.getId()).forEach(achievement -> {

            int id = achievement.getId();
            long required = achievement.getRequired();
            List<AchievementReward> rewards = achievement.getRewards();

            long progress = achievementUser.getAchievementProgress(category.getId());
            boolean achievementClaimed = achievementUser.isAchievementClaimed(achievement);

            String status = progress < required ? "&cNie możesz tego jeszcze odebrać!"
                    : achievementClaimed ? "&aOdebrałeś już to osiągnięcie!"
                    : "&aKliknij tutaj aby odebrać!";

            String progressBar = TextUtil.progress((int) progress, (int) required, 10, "▋", "&a", "&c");
            double percentProgress = RoundUtil.round((double) progress / required * 100.0, 2);

            FlameItemBuilder of = FlameItemBuilder.of(category.getIcon());
            of.name("&b&l" + category.getFriendlyName() + " &8#" + id)
                    .lore(
                            "&cOdbieraj osiągnięcia i zdobywaj",
                            "&cnagrody w postaci fragmentów, kluczów itp.",
                            "",
                            "&3&lNagrody:"
                    );

            for (AchievementReward reward : achievement.getRewards()) {
                String friendlyName = reward.getFriendlyName();
                of.appendLore(" &8- " + friendlyName);
            }

            of.appendLore("");

            if ("spend_time".equals(category.getId())) {
                String timeProgress = TimeUtil.formatTimeSimple(progress);
                String timeRequired = TimeUtil.formatTimeSimple(required);
                of.appendLore(" &7Posiadasz: &f" + timeProgress + "&8/&3" + timeRequired);
            } else {
                of.appendLore(" &7Posiadasz: &f" + progress + "&8/&3" + required);
            }

            of.appendLore(
                    " &7Progres: &8[" + progressBar + "&8] &f" + percentProgress + "%",
                    "",
                    status
            );

            gui.addItem(of.asGuiItem(event -> {
                if (progress < required) {
                    BukkitMessage.from("&cNie możesz tego jeszcze odebrać.").send(player);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    return;
                }

                if (achievementClaimed) {
                    BukkitMessage.from("&cOdebrałeś już to osiągnięcie!").send(player);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    return;
                }

                achievementUser.addClaimedAchievement(achievement);
                achievementUserRepository.save(achievementUser);
                for (AchievementReward reward : rewards) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.getCommand().replace("{player}", player.getName()));
                }
                this.networkMessageService.send(
                        "&7Gracz &f" + player.getName() + " &7odebrał osiągnięcie &b&l" + category.getFriendlyName() + " &8#" + achievement.getId(),
                        new NetworkMessageFilterBuilder()
                                .targetServerCategory(this.networkServerCache.getCurrent().getCategory())
                                .build(),
                        NetworkMessageType.CHAT
                );

                openAchievements(category, achievementUser);
            }));
        });

        gui.open(player);
    }

    private void border(BaseGui gui) {
        gui.setItem(List.of(1, 2, 6, 7, 20, 21, 23, 24),FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).asGuiItem());
        gui.setItem(List.of(0, 8, 18, 19, 25, 26, 3, 4, 5, 22), FlameItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).asGuiItem());

    }
}