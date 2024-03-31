package io.github.flamehub.lobby.daily;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.commons.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public final class DailyGui {

    Random random = new Random();


    private final FlameDispatcher flameDispatcher;
    private final DailyUserCache dailyUserCache;
    private final DailyUserRepository dailyUserRepository;

    public DailyGui(FlameDispatcher flameDispatcher, DailyUserCache dailyUserCache, DailyUserRepository dailyUserRepository) {
        this.flameDispatcher = flameDispatcher;
        this.dailyUserCache = dailyUserCache;
        this.dailyUserRepository = dailyUserRepository;
    }

    public void open(Player player) {

        Gui gui = Gui.gui()
                .type(GuiType.HOPPER)
                .title(TextUtil.parse("        &#FBC378ᴄᴏᴅᴢɪᴇɴɴᴀ ɴᴀɢʀᴏᴅᴀ"))
                .disableAllInteractions()
                .create();

        gui.getFiller().fill(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());

        gui.setItem(2, FlameItemBuilder.of(SkullBuilder.create("5cd5c9b41afe4ddfa06001f78c781d1a39d8e1ba9d84bb14a080a7a219efde3"))
                .name("&#FBC378Codzienna Nagroda")
                .lore(
                        "",
                        "&8» &7Odbieraj codziennie darmowe &#FBC378vPLN",
                        "&8» &7w wysokości kwoty od &#FBC3780.05vPLN &7do &#FBC3780.25vPLN",
                        ""
                )
                .asGuiItem(event -> {

                    DailyUser dailyUser = this.dailyUserCache.findByUniqueId(player.getUniqueId());
                    if (dailyUser.getNextReceive() != null && Instant.now().isBefore(dailyUser.getNextReceive())) {
                        BukkitMessage.from("&cNastępny raz nagrodę możesz odebrać za: &4" + TimeUtil.formatTime(Duration.between(Instant.now(), dailyUser.getNextReceive())))
                                .send(player);
                        return;
                    }

                    gui.close(player);
                    double randomNumber = 0.05 + (0.25 - 0.05) * random.nextDouble();
                    randomNumber = RoundUtil.round(randomNumber, 2);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "awallet add " + player.getName() + " " + randomNumber);
                    TitleUtil.title(player, "&#FBC378Nagroda", "&7Pomyślnie odebrałeś &#FBC378" + randomNumber + "&#FBC378vPLN", 10, 60, 20);
                    dailyUser.setNextReceive(Instant.now().plus(24, ChronoUnit.HOURS));
                    this.flameDispatcher.dispatchAsync(() -> this.dailyUserRepository.save(dailyUser));

                }));

        gui.open(player);

    }
}
