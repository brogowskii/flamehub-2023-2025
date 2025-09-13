package io.github.flamehub.coinflip;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.coinflip.user.CoinFlipUser;
import io.github.flamehub.coinflip.user.CoinFlipUserCache;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.HexUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServerContext;
import io.github.flamehub.commons.util.TimeUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public final class CoinFlipGui {

  private final RedisMessenger redisMessenger;
  private final NetworkMessageService networkMessageService;
  private final CoinFlipConfig coinFlipConfig;
  private final CoinFlipGameCache coinFlipGameCache;
  private final CoinFlipUserCache coinFlipUserCache;

  public CoinFlipGui(
      final RedisMessenger redisMessenger,
      final NetworkMessageService networkMessageService,
      final CoinFlipConfig coinFlipConfig,
      final CoinFlipGameCache coinFlipGameCache,
      final CoinFlipUserCache coinFlipUserCache
  ) {
    this.redisMessenger = redisMessenger;
    this.networkMessageService = networkMessageService;
    this.coinFlipConfig = coinFlipConfig;
    this.coinFlipGameCache = coinFlipGameCache;
    this.coinFlipUserCache = coinFlipUserCache;
  }

  public static void fillGui6(final BaseGui gui) {
    gui.getFiller()
        .fillBorder(FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(0, 8, 45, 53),
        FlameItemBuilder.of(Material.PINK_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(1, 7, 9, 17, 36, 44, 46, 52),
        FlameItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(49, FlameItemBuilder.of(Material.AIR).asGuiItem());
    gui.setItem(4, FlameItemBuilder.of(Material.AIR).asGuiItem());
  }

  public void open(final Player player) {

    final Gui gui = Gui.gui()
        .title(TextUtil.parse(
            "&#CB2EBA&lᴄ&#D430C2&lᴏ&#DD32CA&lɪ&#E533D2&lɴ&#EE35DA&lꜰ&#E233CF&lʟ&#D730C5&lɪ&#CB2EBA&lᴘ"))
        .rows(6)
        .disableAllInteractions()
        .create();
    fillGui6(gui);

    final Collection<CoinFlipGame> values = coinFlipGameCache.values();
    int i = values.size();
    for (final CoinFlipGame value : values.stream()
        .sorted((o1, o2) -> Long.compare(o2.getCreateDate().toEpochMilli(),
            o1.getCreateDate().toEpochMilli()))
        .toList()) {

      final CoinFlipPlayer creator = value.getCreator();
      final CoinFlipUser creatorUser = coinFlipUserCache.findByUniqueId(creator.getUniqueId());
      final CoinFlipUser enemyUser = coinFlipUserCache.findByUniqueId(player.getUniqueId());

      final ItemStack currency = coinFlipConfig.getCurrency().clone();
      final Component displayName = currency.getItemMeta().displayName();
      final String serializedDisplayName = TextUtil.serialize(displayName);
      final String coloredCreatorName = HexUtil.interpolateColors(creator.getName(), "#CB2EBA",
          "#EE35DA", false);
      final OfflinePlayer offlineCreator = Bukkit.getOfflinePlayer(creatorUser.getUniqueId());
      final PlayerProfile creatorProfile = offlineCreator.getPlayerProfile();
      gui.addItem(FlameItemBuilder.of(create(creatorProfile))
          .name(
              "&#CB2EBA&lᴄ&#D430C2&lᴏ&#DD32CA&lɪ&#E533D2&lɴ&#EE35DA&lꜰ&#E233CF&lʟ&#D730C5&lɪ&#CB2EBA&lᴘ &f&l#"
                  + i--)
          .lore(
              "",
              "&8▶ &fStworzył: " + coloredCreatorName,
              "&8▶ &fStawka: &fx" + value.getBet() + " " + serializedDisplayName,
              "&8▶ &fData utworzenia: &d" + TimeUtil.formatDate(value.getCreateDate()),
              "",
              "&dKliknij, aby zagrać!"
          )
          .asGuiItem(inventoryClickEvent -> {

            final CoinFlipGame game = coinFlipGameCache.get(value.getId());
            if (game == null || game.getOpponent() != null) {
              BukkitMessage.from(
                      "&cWystąpił błąd. Możliwe, że gra już się rozpoczęła lub została usunięta")
                  .deliver(player);
              open(player);
              return;
            }

            if (game.getCreator().getUniqueId().equals(player.getUniqueId())) {
              coinFlipUserCache.update(creator.getUniqueId(), user -> {
                user.setDeposit(user.getDeposit() + value.getBet());
              });

              coinFlipGameCache.remove(game.getId());
              BukkitMessage.from(
                      "&aPomyślnie usunięto grę. Aby odebrać odłamki wpisz &2/coinflip odbierz")
                  .deliver(player);
              open(player);
              return;
            }

            currency.setAmount(value.getBet());
            if (!player.getInventory().containsAtLeast(currency, value.getBet())) {
              BukkitMessage.from("&cNie posiadasz wystarczającej ilości waluty aby zagrać")
                  .deliver(player);
              player.closeInventory();
              return;
            }
            player.getInventory().removeItem(currency);

            coinFlipGameCache.update(game.getId(), entity -> {
              game.setOpponent(new CoinFlipPlayer(player.getName(), player.getUniqueId()));
            });

            final PlayerProfile opponentProfile = player.getPlayerProfile();
            final boolean creatorWins = ThreadLocalRandom.current().nextBoolean();
            final ItemStack winningHead =
                creatorWins ? create(creatorProfile) : create(opponentProfile);
            final ItemStack losingHead =
                creatorWins ? create(opponentProfile) : create(creatorProfile);

            final UUID winner = creatorWins ? creatorUser.getUniqueId() : enemyUser.getUniqueId();
            final UUID loser = creatorWins ? enemyUser.getUniqueId() : creatorUser.getUniqueId();

            final Consumer<ItemStack> consumer = itemStack -> {

              final int bet = (int) ((value.getBet() * 2) * 0.9);
              coinFlipUserCache.update(winner, user -> user.setDeposit(user.getDeposit() + bet));

              final CoinFlipUser loserUser = coinFlipUserCache.findByUniqueId(loser);
              final CoinFlipUser winnerUser = coinFlipUserCache.findByUniqueId(winner);

              networkMessageService.sendAsync(
                  List.of(
                      "",
                      "&5☯ &8| &#CB2EBA&l/" + CoinFlipConstants.COINFLIP_PREFIX + " &8▶ &fGracz "
                          + HexUtil.interpolateColors(winnerUser.getName(), "#CB2EBA", "#EE35DA",
                          false) + " &fwygrał",
                      "&frzut monetą z graczem " + HexUtil.interpolateColors(loserUser.getName(),
                          "#CB2EBA", "#EE35DA", false) + " &fo &dx&l" + game.getBet()
                          + " &ffragmentów!",
                      ""
                  ),
                  NetworkMessageFilter.builder()
                      .idForHide("coinflip")
                      .targetServerCategory(NetworkServerContext.CURRENT_CATEGORY)
                      .build(),
                  NetworkMessageType.CHAT
              );

              coinFlipGameCache.remove(game.getId());

            };

            final CoinFlipSpinGui spinGui = new CoinFlipSpinGui(winningHead, losingHead, consumer);
            spinGui.spin(player);

            redisMessenger.publish(
                NetworkServerContext.CURRENT_CATEGORY + ":coinflip",
                new CoinFlipSpinPacket(
                    creatorUser.getUniqueId(),
                    winningHead.serializeAsBytes(),
                    losingHead.serializeAsBytes())
            );

          }));
    }

    gui.open(player);

  }

  public static ItemStack create(final @NotNull PlayerProfile playerProfile) {
    final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    final SkullMeta meta = (SkullMeta) head.getItemMeta();
    meta.setPlayerProfile(playerProfile);
    head.setItemMeta(meta);
    return head;
  }

}
