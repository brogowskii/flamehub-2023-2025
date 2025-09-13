package io.github.flamehub.lobby.daily;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class DailyGui {

  public static final String TITLE_GRADIENT =
      "&#1AAB5E&lᴄ&#1BAE60&lᴏ&#1CB062&lᴅ&#1DB363&lᴢ&#1EB665&lɪ&#1FB867&lᴇ&#20BB69&lɴ&#21BE6B&lɴ&#23C16D&lᴀ &#25C670&lɴ&#26C972&lᴀ&#27CB74&lɢ&#28CE76&lʀ&#29D177&lᴏ&#2AD379&lᴅ&#2BD67B&lᴀ";

  private final FlameDispatcher flameDispatcher;
  private final DailyUserCache dailyUserCache;
  private final DailyUserRepository dailyUserRepository;
  private final Random random = new Random();

  public DailyGui(
      final FlameDispatcher flameDispatcher,
      final DailyUserCache dailyUserCache,
      final DailyUserRepository dailyUserRepository
  ) {
    this.flameDispatcher = flameDispatcher;
    this.dailyUserCache = dailyUserCache;
    this.dailyUserRepository = dailyUserRepository;
  }

  public void open(final Player player) {

    final DailyUser dailyUser = dailyUserCache.findByUniqueId(player.getUniqueId());

    final Instant now = Instant.now();
    final Instant next = dailyUser.getNextReceive();
    final Duration remaining = (next != null && now.isBefore(next))
        ? Duration.between(now, next)
        : Duration.ZERO;
    final boolean onCooldown = !remaining.isZero() && !remaining.isNegative();

    final Gui gui = Gui.gui()
        .type(GuiType.HOPPER)
        .title(TextUtil.parse("     " + TITLE_GRADIENT))
        .disableAllInteractions()
        .create();

    if (onCooldown) {
      final String timeLeft = TimeUtil.formatTime(remaining);
      for (int slot = 0; slot < 5; slot++) {
        gui.setItem(slot, FlameItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
            .name("")
            .lore("", "&cNagroda dostępna za: &4" + timeLeft)
            .asGuiItem(event -> event.setCancelled(true)));
      }
      gui.open(player);
      return;
    }

    final double[] amounts = new double[5];
    for (int i = 0; i < 5; i++) {
      amounts[i] = RoundUtil.round(0.05 + (0.30 - 0.05) * random.nextDouble(), 2);
    }

    final boolean[] claimed = {false};
    final int[] chosenIndex = {-1};

    final Runnable renderReveal = () -> {
      for (int slot = 0; slot < 5; slot++) {
        final boolean isChosen = (slot == chosenIndex[0]);
        final String amountStr = String.format(Locale.US, "%.2f", amounts[slot]);

        final FlameItemBuilder builder = FlameItemBuilder.of(
                isChosen ? Material.GREEN_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE)
            .name(isChosen ? "&aTwój wybór" : "&7Inny slot")
            .lore(
                "",
                isChosen
                    ? "&8➥ &fOtrzymałeś: &e" + amountStr + " &6ᴠᴘʟɴ"
                    : "&8➥ &fW tym slocie było: &e" + amountStr + " &6ᴠᴘʟɴ",
                ""
            );
        if (isChosen) builder.glow();

        gui.setItem(slot, builder.asGuiItem(event -> event.setCancelled(true)));
      }

      gui.update();
    };

    // RENDER przed WYBOREM — szare szyby + handler kliknięcia
    final Runnable renderInitial = () -> {
      for (int slot = 0; slot < 5; slot++) {
        final int finalSlot = slot;
        gui.setItem(slot, FlameItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE)
            .name(TITLE_GRADIENT)
            .lore(
                "",
                "&8➥ &fKliknij, aby wylosować &6ᴠᴘʟɴ",
                "&8➥ &fZakres: &e0.05vPLN &8– &60.30vPLN",
                ""
            )
            .asGuiItem(event -> {
              event.setCancelled(true);
              if (claimed[0]) return;

              final DailyUser du = dailyUserCache.findByUniqueId(player.getUniqueId());
              if (du == null) {
                BukkitMessage.from("&cWystąpił problem z Twoim profilem. Spróbuj ponownie za chwilę.")
                    .deliver(player);
                return;
              }

              if (du.getNextReceive() != null && Instant.now().isBefore(du.getNextReceive())) {
                BukkitMessage.from("&cNastępny raz nagrodę możesz odebrać za: &4" +
                        TimeUtil.formatTime(Duration.between(Instant.now(), du.getNextReceive())))
                    .deliver(player);
                return;
              }

              claimed[0] = true;
              chosenIndex[0] = finalSlot;

              // Nagroda
              final double reward = amounts[finalSlot];
              final String rewardStr = String.format(Locale.US, "%.2f", reward);
              Bukkit.getServer().dispatchCommand(
                  Bukkit.getConsoleSender(),
                  "awallet add " + player.getName() + " " + rewardStr
              );

              // Feedback
              TitleUtil.title(
                  player,
                  TITLE_GRADIENT,
                  "&7Pomyślnie odebrałeś &e" + rewardStr + " &6ᴠᴘʟɴ",
                  10, 60, 20
              );
              player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.1f);

              du.setNextReceive(Instant.now().plus(24, ChronoUnit.HOURS));
              flameDispatcher.dispatchAsync(() -> dailyUserRepository.save(du));

              Bukkit.getScheduler().runTaskLater(
                  CommonsPlugin.getInstance(),
                  renderReveal,
                  5L
              );
            }));
      }
    };

    renderInitial.run();
    gui.open(player);
  }
}