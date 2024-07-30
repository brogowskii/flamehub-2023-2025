package io.github.flamehub.daily.reward;

import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.daily.reward.user.DailyRewardUser;
import io.github.flamehub.daily.reward.user.DailyRewardUserCache;
import io.github.flamehub.daily.reward.user.DailyRewardUserRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class DailyRewardGui {

  private final FlameDispatcher flameDispatcher;
  private final DailyRewardConfig dailyRewardConfig;
  private final DailyRewardUserCache dailyRewardUserCache;
  private final DailyRewardUserRepository dailyRewardUserRepository;

  public DailyRewardGui(FlameDispatcher flameDispatcher, DailyRewardConfig dailyRewardConfig,
      DailyRewardUserCache dailyRewardUserCache,
      DailyRewardUserRepository dailyRewardUserRepository) {
    this.flameDispatcher = flameDispatcher;
    this.dailyRewardConfig = dailyRewardConfig;
    this.dailyRewardUserCache = dailyRewardUserCache;
    this.dailyRewardUserRepository = dailyRewardUserRepository;
  }

  public void open(Player player) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    Gui gui = Gui.gui()
        .rows(6)
        .title(TextUtil.parse(
            "&#00C393⚓ &8| &#00C393&lᴄ&#01CA99&lᴏ&#02D19E&lᴅ&#03D8A4&lᴢ&#04DFAA&lɪ&#05E6AF&lᴇ&#06EDB5&lɴ&#07F4BA&lɴ&#08FBC0&lᴀ &#06EDB5&lɴ&#05E6AF&lᴀ&#04DFAA&lɢ&#03D8A4&lʀ&#02D19E&lᴏ&#01CA99&lᴅ&#00C393&lᴀ"))
        .disableAllInteractions()
        .create();
    GuiHelper.fillGui6(gui);

    DailyRewardUser dailyRewardUser = this.dailyRewardUserCache.findByUniqueId(
        player.getUniqueId());
    int currentStreak = dailyRewardUser.getCurrentStreak();
    for (DailyReward dailyReward : this.dailyRewardConfig.getDailyRewardMap().values()) {

      int nextStreak = currentStreak + 1;
      if (nextStreak > this.dailyRewardConfig.getDailyRewardMap().size()) {
        nextStreak = currentStreak;
      }

      boolean isClaimedStreak = dailyRewardUser.getClaimed().contains(dailyReward.getStreak());
      boolean isClaimingStreak = nextStreak == dailyReward.getStreak();
      String loreAboutClaim;
      FlameItemBuilder flameItemBuilder;
      if (isClaimingStreak) {

        if (dailyRewardUser.getNextClaimTime().isBefore(Instant.now())) {
          flameItemBuilder = FlameItemBuilder.of(Material.HOPPER_MINECART);
          flameItemBuilder.name("&6Dzień #" + dailyReward.getStreak() + " &8- &fOdbierz teraz!");
          loreAboutClaim = "&eKliknij, aby odebrać nagrodę.";
        } else {
          flameItemBuilder = FlameItemBuilder.of(Material.TNT_MINECART);
          flameItemBuilder.name("&6Dzień #" + dailyReward.getStreak() + " &8- &fOdbierz Jutro!");
          loreAboutClaim = "&eNagrodę możesz odebrać od godziny &600:00&e!";
        }

      } else if (isClaimedStreak) {
        flameItemBuilder = FlameItemBuilder.of(Material.MINECART).
            name("&aDzień #" + dailyReward.getStreak() + " &8- &fJuż odebrałeś!");
        loreAboutClaim = "&cOdebrałeś już tą nagrodę.";
      } else {
        flameItemBuilder = FlameItemBuilder.of(Material.CHEST_MINECART).
            name("&cDzień #" + dailyReward.getStreak() + " &8- &fOdbierz wkrótce!");
        loreAboutClaim = "&cTą nagrodę będziesz mógł odebrać wkrótce.";
      }

      flameItemBuilder.lore(
          "&cLoguj się codziennie na serwerze",
          "&ci zgarniaj itemki za darmo!",
          "",
          "&3&lNagrody:"
      );

      for (String reward : dailyReward.getRewards()) {
        flameItemBuilder.appendLore(" &8- " + reward);
      }

      flameItemBuilder.appendLore(
          "",
          loreAboutClaim
      );

      gui.addItem(flameItemBuilder.asGuiItem(event -> {

        if (!isClaimingStreak) {
          player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
          return;
        }

        if (dailyRewardUser.getNextClaimTime().isAfter(Instant.now())) {
          BukkitMessage.from(
              "&aTą nagrodę możesz odebrać dopiero o godzinie 00:00 następnego dnia.").send(player);
          player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
          return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextDayStart = now.toLocalDate().atStartOfDay().plusDays(1);
        Instant nextDayInstant = ZonedDateTime.of(nextDayStart, ZoneId.of("UTC")).toInstant();

        dailyRewardUser.getClaimed().add(dailyReward.getStreak());
        dailyRewardUser.setNextClaimTime(nextDayInstant);
        dailyRewardUser.setCurrentStreak(dailyRewardUser.getCurrentStreak() + 1);
        if (dailyRewardUser.getCurrentStreak() >= this.dailyRewardConfig.getDailyRewardMap()
            .size()) {
          dailyRewardUser.setCurrentStreak(0);
          dailyRewardUser.getClaimed().clear();
        }

        for (String command : dailyReward.getCommand()) {
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
              command.replace("{player}", player.getName()));
        }

        this.flameDispatcher.dispatchAsync(
            () -> this.dailyRewardUserRepository.save(dailyRewardUser));
        open(player);
        BukkitMessage.from("&aPomyślnie odebrano codzienną nagrodę.").send(player);

      }));

    }

    gui.open(player);
  }
}
