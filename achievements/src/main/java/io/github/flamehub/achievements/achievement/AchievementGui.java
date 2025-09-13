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
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageFilterBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class AchievementGui {

  private final Player player;

  private final AchievementConfig achievementConfig;
  private final AchievementService achievementService;
  private final AchievementUserCache achievementUserCache;
  private final AchievementUserRepository achievementUserRepository;
  private final NetworkMessageService networkMessageService;
  private final NetworkServerFacade networkServerFacade;

  public AchievementGui(final Player player, final AchievementConfig achievementConfig,
      final AchievementService achievementService, final AchievementUserCache achievementUserCache,
      final AchievementUserRepository achievementUserRepository,
      final NetworkMessageService networkMessageService, final NetworkServerFacade networkServerFacade) {
    this.player = player;
    this.achievementConfig = achievementConfig;
    this.achievementService = achievementService;
    this.achievementUserCache = achievementUserCache;
    this.achievementUserRepository = achievementUserRepository;
    this.networkMessageService = networkMessageService;
    this.networkServerFacade = networkServerFacade;
  }

  public void openSelection() {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    final Gui gui = Gui.gui()
        .title(TextUtil.parse(
            "&#1cd9ce✂ &8| &#1cd9ce&lᴏ&#1de1d5&ls&#1ee8dc&lɪ&#1ff0e4&lᴀ&#20f7eb&lɢ&#21fff2&lɴ&#20f7eb&lɪ&#1ff0e4&lᴇ&#1ee8dc&lᴄ&#1de1d5&lɪ&#1cd9ce&lᴀ"))
        .rows(5)
        .disableAllInteractions()
        .create();
    GuiHelper.fillGui5(gui);

    final Map<String, AchievementCategory> achievementCategories = achievementConfig.getAchievementCategories();
    final AchievementUser user = achievementUserCache.findByUniqueId(player.getUniqueId());

    for (final AchievementCategory value : achievementCategories.values()) {
      final int size = achievementService.size(value.getId());
      final long claimedAchievementsCount = user.claimedAchievementsCount(value);
      final boolean availableToClaim =
          achievementService.availableToClaimFromCategory(value.getId(), user) > 0;

      final List<String> availableToClaimInfo = availableToClaim ?
          Arrays.asList(" &ePosiadasz możliwe do odebrania", " &6osiągnięcie &ew tej kategorii.") :
          Arrays.asList(" &cNie posiadasz możliwych do odebrania",
              " &4osiągnięć &cw tej kategorii.");

      final FlameItemBuilder builder = FlameItemBuilder.of(value.getIcon())
          .name("&b&l" + value.getFriendlyName())
          .lore("", " &7Odebrane osiągnięcia: &f" + claimedAchievementsCount + "&8/&7" + size, "");

      builder
          .appendLore(availableToClaimInfo)
          .appendLore("", "&eKliknij, aby &6przeglądać &eosiągnięcia w tej kategorii.");

      gui.setItem(value.getSlot(), builder.asGuiItem(event -> openAchievements(value, user)));
    }

    gui.open(player);
  }


  private void openAchievements(final AchievementCategory category, final AchievementUser achievementUser) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    final PaginatedGui gui = Gui.paginated()
        .title(TextUtil.parse("&8&l" + category.getFriendlyName()))
        .rows(3)
        .pageSize(9)
        .disableAllInteractions()
        .create();
    border(gui);

    gui.setItem(3, 3, FlameItemBuilder.of(
            SkullBuilder.create("f84f597131bbe25dc058af888cb29831f79599bc67c95c802925ce4afba332fc"))
        .name("&cPoprzednia strona")
        .asGuiItem(inventoryClickEvent -> gui.previous()));

    gui.setItem(3, 7, FlameItemBuilder.of(
            SkullBuilder.create("fcfe8845a8d5e635fb87728ccc93895d42b4fc2e6a53f1ba78c845225822"))
        .name("&aNastępna strona")
        .asGuiItem(inventoryClickEvent -> gui.next()));

    gui.setItem(3, 5, FlameItemBuilder.of(Material.RED_CONCRETE)
        .name("&c&lPowrót")
        .lore(
            "",
            " &fKliknij, aby powrócić na poprzednią stronę.",
            ""
        )
        .asGuiItem(event -> {
          openSelection();
        }));

    achievementService.findByCategories(category.getId()).forEach(achievement -> {

      final int id = achievement.getId();
      final long required = achievement.getRequired();
      final List<AchievementReward> rewards = achievement.getRewards();

      final long progress = achievementUser.getAchievementProgress(category.getId());
      final boolean achievementClaimed = achievementUser.isAchievementClaimed(achievement);

      final String status = progress < required ? "&cNie możesz tego jeszcze odebrać!"
          : achievementClaimed ? "&aOdebrałeś już to osiągnięcie!"
              : "&aKliknij tutaj aby odebrać!";

      final String progressBar = TextUtil.progress((int) progress, (int) required, 10, "▋", "&a", "&c");
      final double percentProgress = RoundUtil.round((double) progress / required * 100.0, 2);

      final FlameItemBuilder of = FlameItemBuilder.of(category.getIcon());
      of.name("&b&l" + category.getFriendlyName() + " &8#" + id)
          .lore(
              "&cOdbieraj osiągnięcia i zdobywaj",
              "&cnagrody w postaci fragmentów, kluczów itp.",
              "",
              "&3&lNagrody:"
          );

      for (final AchievementReward reward : achievement.getRewards()) {
        final String friendlyName = reward.getFriendlyName();
        of.appendLore(" &8- " + friendlyName);
      }

      of.appendLore("");

      if ("spend_time".equals(category.getId())) {
        final String timeProgress = TimeUtil.formatTimeSimple(progress);
        final String timeRequired = TimeUtil.formatTimeSimple(required);
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
          BukkitMessage.from("&cNie możesz tego jeszcze odebrać.").deliver(player);
          player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
          return;
        }

        if (achievementClaimed) {
          BukkitMessage.from("&cOdebrałeś już to osiągnięcie!").deliver(player);
          player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
          return;
        }

        achievementUser.addClaimedAchievement(achievement);
        achievementUserRepository.save(achievementUser);
        for (final AchievementReward reward : rewards) {
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
              reward.getCommand().replace("{player}", player.getName()));
        }
        networkMessageService.send(
            "&#1ee8dc✂ &8| &fGracz &#1ee8dc" + player.getName()
                + " &fodebrał osiągnięcie &#1ee8dc&l" + category.getFriendlyName() + " &8#"
                + achievement.getId(),
            new NetworkMessageFilterBuilder()
                .targetServerCategory(networkServerFacade.getCurrent().getCategory())
                .build(),
            NetworkMessageType.CHAT
        );

        openAchievements(category, achievementUser);
      }));
    });

    gui.open(player);
  }

  private void border(final BaseGui gui) {
    gui.setItem(List.of(1, 2, 6, 7, 20, 21, 23, 24),
        FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).asGuiItem());
    gui.setItem(List.of(0, 8, 18, 19, 25, 26, 3, 4, 5, 22),
        FlameItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).asGuiItem());

  }
}