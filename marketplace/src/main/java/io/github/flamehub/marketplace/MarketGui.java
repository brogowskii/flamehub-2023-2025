package io.github.flamehub.marketplace;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.bukkit.util.SkullBuilder;
import io.github.flamehub.commons.bukkit.util.TitleUtil;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageFilterBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.economy.EconomyFacade;
import io.github.flamehub.marketplace.category.MarketCategory;
import io.github.flamehub.marketplace.category.MarketCategoryConfig;
import io.github.flamehub.marketplace.category.MarketCategoryWrapper;
import io.github.flamehub.marketplace.offer.MarketOffer;
import io.github.flamehub.marketplace.offer.MarketOfferCache;
import io.github.flamehub.marketplace.offer.MarketOfferFilter;
import io.github.flamehub.marketplace.offer.MarketOfferItem;
import io.github.flamehub.marketplace.offer.MarketOfferPageSorter;
import io.github.flamehub.marketplace.offer.MarketOfferRedisStorage;
import io.github.flamehub.marketplace.offer.MarketOfferRemove;
import io.github.flamehub.marketplace.offer.MarketOfferRepository;
import io.github.flamehub.marketplace.offer.MarketOfferSort;
import io.github.flamehub.marketplace.offer.MarketOfferSorter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class MarketGui {

  private final Map<UUID, Long> cooldown = new ConcurrentHashMap<>();

  private final FlameDispatcher flameDispatcher;
  private final RedisMessenger redisMessenger;

  private final NetworkServerFacade networkServerFacade;
  private final NetworkMessageService networkMessageService;
  private final MarketOfferCache marketOfferCache;
  private final MarketCategoryConfig marketCategoryConfig;
  private final MarketOfferRedisStorage marketOfferRedisStorage;
  private final MarketOfferRepository marketOfferRepository;

  private final EconomyFacade economyFacade;
  private final Player player;
  private final MarketOfferPageSorter pageSorter = new MarketOfferPageSorter();
  private final MarketOfferSorter sorter = new MarketOfferSorter();
  int page = 1;
  private MarketOfferSort sortType = MarketOfferSort.LEVEL;
  private MarketOfferFilter filter = MarketOfferFilter.ALL;
  private MarketCategory category;

  private final Map<UUID, String> playerCurrentWrapper = new HashMap<>();
  private final Map<UUID, MarketCategory> playerCategoryStates = new HashMap<>();

  public MarketGui(
      final FlameDispatcher flameDispatcher,
      final RedisMessenger redisMessenger, final NetworkServerFacade networkServerFacade,
      final NetworkMessageService networkMessageService,
      final MarketOfferCache marketOfferCache,
      final MarketCategoryConfig marketCategoryConfig,
      final MarketOfferRedisStorage marketOfferRedisStorage,
      final MarketOfferRepository marketOfferRepository,
      final EconomyFacade economyFacade,
      final Player player
  ) {
    this.flameDispatcher = flameDispatcher;
    this.redisMessenger = redisMessenger;
    this.networkServerFacade = networkServerFacade;
    this.networkMessageService = networkMessageService;
    this.marketOfferCache = marketOfferCache;
    this.marketCategoryConfig = marketCategoryConfig;
    this.marketOfferRedisStorage = marketOfferRedisStorage;
    this.marketOfferRepository = marketOfferRepository;
    this.economyFacade = economyFacade;
    this.player = player;
  }

  public static void fillGui6(final BaseGui gui) {
    gui.setItem(List.of(8, 16, 34, 36, 38, 40, 42, 44, 7, 25, 37, 39, 41, 43, 51, 52, 53),
        FlameItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).asGuiItem());
  }

  private void updateTitle(final BaseGui gui, final int currentPageNum, final int maxPageNum) {
    gui.updateTitle(TextUtil.legacyColor(BukkitMessage.from(
            "&#29ADCF✉ &8| &#29ADCF&lʀ&#29AACF&lʏ&#29A7CF&lɴ&#29A4CF&lᴇ&#29A1CF&lᴋ &8(&f{page}&8/&7{max_page}&8)")
        .with("page", currentPageNum)
        .with("max_page", maxPageNum == 0 ? 1 : maxPageNum)
        .applyFirst())
    );
  }

  boolean isOnCooldown() {
    final Long cooldown = this.cooldown.get(player.getUniqueId());
    if (cooldown != null && cooldown + 350 > System.currentTimeMillis()) {
      BukkitMessage.from("&cZwolnij!").deliver(player);
      return true;
    }

    this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());
    return false;
  }

  public void open() {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    final Gui gui = Gui.gui()
        .disableAllInteractions()
        .disableOtherActions()
        .rows(6)
        .title(Component.text(""))
        .create();

    fillGui6(gui);

    gui.setItem(2, 9, FlameItemBuilder.of(Material.CHEST_MINECART)
        .name(
            "&#53AB8B✉ &8| &#53AB8B&lᴛ&#4CB18C&lᴡ&#44B68D&lᴏ&#3DBC8E&lᴊ&#36C28F&lᴇ &#27CD91&lᴏ&#30C690&lꜰ&#39BF8F&lᴇ&#41B98D&lʀ&#4AB28C&lᴛ&#53AB8B&lʏ")
        .lore(
            "",
            "&#27CD91Kliknij, aby zobaczyć swoje oferty."
        )
        .asGuiItem(event -> yourOffers()));

    gui.setItem(1, 9, FlameItemBuilder.of(Material.GLOW_ITEM_FRAME)
        .name("")
        .lore(
            " &#fba90bW&#fbac0di&#fbb00ft&#fbb311a&#fbb713j &#fbba15n&#fcbe18a &#fcc11aR&#fcc51cy&#fcc81en&#fccc20k&#fccf22u &#fcd224S&#fcd626e&#fcd928r&#fcdd2aw&#fce02ce&#fde42fr&#fde731o&#fdeb33w&#fdee35y&#fdf237m&#fdf539!",
            " &fAby wystawić przedmiot użyj: &#fba90b/&#fbac0dr&#fbaf0fy&#fbb311n&#fbb613e&#fbb915k &#fcbc17w&#fcbf18y&#fcc21as&#fcc61ct&#fcc91ea&#fccc20w &#fccf22<&#fcd224c&#fcd526e&#fcd928n&#fcdc2aa&#fcdf2c>",
            "",
            " &fLimity &#fcc11awystawionych przedmiotów &fdla rang:",
            " &8× &f\uE042 &8- &f3 oferty",
            " &8× &f\uE051 &8- &f5 ofert",
            " &8× &f\uE04E &8- &f10 ofert",
            " &8× &f\uE049 &8- &f15 ofert",
            " &8× &f\uE040 &8- &f20 ofert",
            ""
        )
        .asGuiItem());

    gui.setItem(3, 9, FlameItemBuilder.of(Material.HOPPER)
        .glow(sortType != MarketOfferSort.NONE)
        .name(
            "&#29ADCF♻ &8| &#29ADCF&ls&#29A9CF&lᴏ&#29A4CF&lʀ&#29A0CF&lᴛ&#299BCF&lᴏ&#299BCF&lᴡ&#29A0CF&lᴀ&#29A4CF&lɴ&#29A9CF&lɪ&#29ADCF&lᴇ")
        .lore(BukkitMessage.from(
                "",
                " {NONE}",
                " {LEVEL}",
                " {ASCENDING}",
                " {DESCENDING}",
                " {NEWEST}",
                " {OLDEST}",
                "",
                "&bKliknij, aby zmienić filtr sortujący."
            )
            .with("none",
                sortType == MarketOfferSort.NONE ? "&2▶ &aBrak sortowania"
                    : "&#A41D1D▶ &#D44444Brak sortowania")
            .with("level",
                sortType == MarketOfferSort.LEVEL ? "&2▶ &aPoziom"
                    : "&#A41D1D▶ &#D44444Poziom")
            .with("ascending",
                sortType == MarketOfferSort.ASCENDING_PRICE ? "&2▶ &aCena rosnąco"
                    : "&#A41D1D▶ &#D44444Cena rosnąco")
            .with("descending",
                sortType == MarketOfferSort.DESCENDING_PRICE
                    ? "&2▶ &aCena malejąco"
                    : "&#A41D1D▶ &#D44444Cena malejąco")
            .with("newest",
                sortType == MarketOfferSort.NEWEST ? "&2▶ &aNajnowsze"
                    : "&#A41D1D▶ &#D44444Najnowsze")
            .with("oldest",
                sortType == MarketOfferSort.OLDEST ? "&2▶ &aNajstarsze"
                    : "&#A41D1D▶ &#D44444Najstarsze")
            .apply())
        .asGuiItem(event -> {

          if (isOnCooldown()) {
            return;
          }

          this.sortType = sortType.next();
          open();

        }));

    gui.setItem(4, 9, FlameItemBuilder.of(Material.STONECUTTER)
        .name(
            "&#E6EC7F\uD83E\uDEA3 &8| &#E6EC7F&lꜰ&#E8ED85&lɪ&#E9EF8B&lʟ&#EBF091&lᴛ&#ECF297&lʀ&#EEF39D&lᴏ&#ECF297&lᴡ&#EBF091&lᴀ&#E9EF8B&lɴ&#E8ED85&lɪ&#E6EC7F&lᴇ")
        .glow(filter != MarketOfferFilter.ALL)
        .lore(BukkitMessage.from(
                    "",
                    " {ALL}",
                    " {ENOUGH_MONEY}",
                    "",
                    "&#EBF091Kliknij, aby zmienić filtrowanie."
                )
                .with("all",
                    filter == MarketOfferFilter.ALL ? "&2▶ &aWyświetlaj wszystkie przedmioty"
                        : "&#A41D1D▶ &#D44444Wyświetlaj wszystkie przedmioty")
                .with("enough_money",
                    filter == MarketOfferFilter.ENOUGH_MONEY
                        ? "&2▶ &aWyświetlaj tylko te, na które Cię stać"
                        : "&#A41D1D▶ &#D44444Wyświetlaj tylko te, na które Cię stać")
                .apply()
        )
        .asGuiItem(inventoryClickEvent -> {

          if (isOnCooldown()) {
            return;
          }

          this.filter = filter.next();
          open();

        }));

    List<MarketOffer> values = new ArrayList<>(marketOfferCache.values());
    values = values.stream()
        .filter(offer -> offer.getExpirationTime().toEpochMilli() > System.currentTimeMillis())
        .toList();
    values = sorter.sorted(category, sortType, values, filter,
        economyFacade.getBalance(player.getUniqueId()));

    final Map<Integer, List<MarketOffer>> offersByPageMap = pageSorter.offersByPageMap(values);
    List<MarketOffer> marketOffers = offersByPageMap.get(page);
    if (marketOffers == null) {
      marketOffers = new ArrayList<>();
    }

    final int maxPage = (int) Math.ceil((double) values.size() / 28);
    for (final MarketOffer marketOffer : marketOffers) {
      final BigDecimal price = marketOffer.getPrice();
      final double offerPrice = price.doubleValue();
      final MarketOfferItem item = marketOffer.getItem();
      final ItemStack itemStack = item.getItemStack();

      FlameItemBuilder builder = FlameItemBuilder.of(itemStack.clone())
          .appendLore(BukkitMessage.from(
                  "",
                  "&#aef5ff&l&m &#aaedf6&l&m &#a7e5ee&l&m &#a3dde5&l&m &#a0d5dc&l&m &#9ccdd4&l&m &#98c5cb&l&m &#95bdc2&l&m &#91b5ba&l&m &#8dacb1&l&m &#8aa4a8&l&m &#869c9f&l&m &#839497&l&m &#7f8c8e&l&m &#7b8485&l&m &#787c7d&l&m &#747474&l&m &#747474&l&m &#787c7d&l&m &#7b8485&l&m &#7f8c8e&l&m &#839497&l&m &#869c9f&l&m &#8aa4a8&l&m &#8dacb1&l&m &#91b5ba&l&m &#95bdc2&l&m &#98c5cb&l&m &#9ccdd4&l&m &#a0d5dc&l&m &#a3dde5&l&m &#a7e5ee&l&m &#aaedf6&l&m &#aef5ff&l&m ",
                  "",
                  " &e☄ &fSprzedający: &e{SELLER_NAME}",
                  " &6⌚ &fWygasa: &6{EXPIRE}",
                  " &a€ &fCena: &a${PRICE}",
                  ""
              )
              .with("seller_name", marketOffer.getSeller().getName())
              .with("price", NumberConverter.convertNumber(offerPrice))
              .with("expire", TimeUtil.formatDate(marketOffer.getExpirationTime()))
              .apply());

      final double buyerBalance = economyFacade.getBalance(player);
      if (offerPrice > buyerBalance) {
        builder.appendLore(BukkitMessage.from(
                "&#DA0000☹ &cNie posiadasz wystarczającej ilości pieniędzy!",
                "&#DA0000☹ &cBrakuje ci dokładnie: &#DA0000${MISSING_MONEY}"
            )
            .with("missing_money", NumberConverter.convertNumber(offerPrice - buyerBalance))
            .apply());
      } else {
        builder.appendLore("&#38B136☺ &#25D576Kliknij tutaj, aby zakupić.");
      }

      gui.addItem(builder.asGuiItem(event -> {

        if (marketOffer.getSeller().getUniqueId().equals(player.getUniqueId())) {
          BukkitMessage.from("&#DA0000☹ &cNie możesz kupić własnej oferty!").deliver(player);
          player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
          return;
        }

        confirmBuy(marketOffer, itemStack);
      }));
    }

    if (page - 1 >= 1) {
      gui.setItem(6, 8, FlameItemBuilder.of(
              SkullBuilder.create("6768edc28853c4244dbc6eeb63bd49ed568ca22a852a0a578b2f2f9fabe70"))
          .name("&cᴘᴏᴘʀᴢᴇᴅɴɪᴀ sᴛʀᴏɴᴀ")
          .amount(Math.max(page - 1, 1))
          .asGuiItem(event -> {

            if (isOnCooldown()) {
              return;
            }
            page = page - 1;
            open();
          }));
    }

    if (page < maxPage) {
      gui.setItem(6, 9, FlameItemBuilder.of(
              SkullBuilder.create("6ff55f1b32c3435ac1ab3e5e535c50b527285da716e54fe701c9b59352afc1c"))
          .amount(page + 1)
          .name("&aɴᴀsᴛᴇᴘɴᴀ sᴛʀᴏɴᴀ")
          .asGuiItem(event -> {

            if (isOnCooldown()) {
              return;
            }

            page = page + 1;
            open();

          }));
    }

    for (final MarketCategoryWrapper wrapper : marketCategoryConfig.getMarketCategoryWrappers()) {

      final Material material = wrapper.getMaterial();
      final int slot = wrapper.getSlot();
      final List<MarketCategory> categories = wrapper.getMarketCategories();
      final FlameItemBuilder builder = FlameItemBuilder.of(material).name(wrapper.getName());
      builder.appendLore("");

      boolean isSelectedWrapper = category != null && wrapper.getId()
          .equals(playerCurrentWrapper.get(player.getUniqueId()));
      for (final MarketCategory marketCategory : categories) {
        builder.appendLore(
            (category != null && category.getId().equals(marketCategory.getId()))
                ? " &2▶ &a" + marketCategory.getFriendlyName()
                : " &#A41D1D▶ &#D44444" + marketCategory.getFriendlyName()
        );
      }

      builder.appendLore(
          "",
          "&fKliknij lewym, aby wybrać kategorię."
      );

      if (isSelectedWrapper) {
        builder.glow();
      }

      gui.setItem(slot, builder.asGuiItem(inventoryClickEvent -> {

        if (isOnCooldown()) {
          return;
        }

        if (wrapper.getId().equals("all")) {
          playerCurrentWrapper.clear();
          playerCategoryStates.clear();
          category = null;
          page = 1;
          open();
          return;
        }

        final UUID playerId = inventoryClickEvent.getWhoClicked().getUniqueId();
        final String wrapperId = wrapper.getId();

        if (!wrapperId.equals(playerCurrentWrapper.get(playerId))) {
          playerCurrentWrapper.put(playerId, wrapperId);
          playerCategoryStates.put(playerId, null);
        }

        final MarketCategory currentCategory = playerCategoryStates.get(playerId);
        final int nextIndex = (currentCategory == null) ? 0
            : (categories.indexOf(currentCategory) + 1) % categories.size();
        final MarketCategory nextCategory = categories.get(nextIndex);

        if (nextCategory != null) {
          playerCategoryStates.put(playerId, nextCategory);
          category = nextCategory;
        } else {
          category = null;
        }

        page = 1;
        open();
      }));

      final FlameItemBuilder glassPaneBuilder = FlameItemBuilder
          .of(Material.GREEN_STAINED_GLASS_PANE)
          .name("");
      if (category != null && categories.contains(category)) {
        gui.setItem(slot - 9, glassPaneBuilder.asGuiItem());
      }
    }

    updateTitle(gui, page, maxPage);
    gui.setDefaultClickAction(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    gui.open(player);

  }

  private void yourOffers() {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    final Gui gui = Gui.gui()
        .title(TextUtil.parse(
            "&#29ADCF✉ &8| &#29ADCF&lᴛ&#29A9CF&lᴡ&#29A5CF&lᴏ&#29A1CF&lᴊ&#299DCF&lᴇ &#2999CF&lᴏ&#299DCF&lғ&#29A1CF&lᴇ&#29A5CF&lʀ&#29A9CF&lᴛ&#29ADCF&lʏ"))
        .rows(5)
        .disableAllInteractions()
        .create();

    final List<MarketOffer> marketOffers = marketOfferCache.values().stream()
        .filter(offer -> offer.getSeller().getUniqueId().equals(player.getUniqueId()))
        .toList();

    for (final MarketOffer offer : marketOffers) {

      final double price = offer.getPrice().doubleValue();
      final ItemStack itemStack = offer.getItem().getItemStack();
      final FlameItemBuilder builder = FlameItemBuilder.of(itemStack.clone())
          .appendLore(BukkitMessage.from(
                  "",
                  "&#aef5ff&l&m &#aaedf6&l&m &#a7e5ee&l&m &#a3dde5&l&m &#a0d5dc&l&m &#9ccdd4&l&m &#98c5cb&l&m &#95bdc2&l&m &#91b5ba&l&m &#8dacb1&l&m &#8aa4a8&l&m &#869c9f&l&m &#839497&l&m &#7f8c8e&l&m &#7b8485&l&m &#787c7d&l&m &#747474&l&m &#747474&l&m &#787c7d&l&m &#7b8485&l&m &#7f8c8e&l&m &#839497&l&m &#869c9f&l&m &#8aa4a8&l&m &#8dacb1&l&m &#91b5ba&l&m &#95bdc2&l&m &#98c5cb&l&m &#9ccdd4&l&m &#a0d5dc&l&m &#a3dde5&l&m &#a7e5ee&l&m &#aaedf6&l&m &#aef5ff&l&m ",
                  "",
                  " &e☄ &fSprzedający: &e{SELLER_NAME}",
                  " &6⌚ &fWygasa: &6{EXPIRE}",
                  " &a€ &fCena: &a${PRICE}",
                  ""
              )
              .with("seller_name", offer.getSeller().getName())
              .with("price", NumberConverter.convertNumber(price))
              .with("expire", TimeUtil.formatDate(offer.getExpirationTime()))
              .apply());

      builder.appendLore("&6⚠ &eKliknij tutaj, aby &6wycofać &eofertę z rynku.");

      gui.addItem(builder.asGuiItem(event -> {

        boolean exists = marketOfferRedisStorage.exists(offer.getOfferId());
        if (exists) {

          InventoryUtil.addItem(player, itemStack.clone());
          TitleUtil.title(player, "&2Sukces!", "&aPomyślnie anulowano ofertę!", 20, 40, 20);
          flameDispatcher.dispatch(player::closeInventory);

            redisMessenger.publish(
                networkServerFacade.getCurrent().getCategory() + "_auctionhouse_actions",
                new MarketOfferRemove(offer.getOfferId())
            );
            marketOfferRedisStorage.remove(offer.getOfferId());
            marketOfferRepository.delete(offer);


        } else {

          TitleUtil.title(player, "&cBłąd!", "&cTen przedmiot prawdopodobnie został wykupiony.",
              20, 40, 20);
          flameDispatcher.dispatch(player::closeInventory);

        }

      }));

    }

    gui.setItem(5, 5, FlameItemBuilder.of(Material.RED_CONCRETE)
        .name("&c&lPowrót")
        .lore(
            "",
            " &7Kliknij, aby powrócić na poprzednią stronę.",
            ""
        )
        .asGuiItem(event -> {
          open();
        }));

    gui.setDefaultClickAction(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    gui.open(player);

  }

  private void confirmBuy(final MarketOffer offer, final ItemStack item) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    final Gui gui = Gui.gui()
        .title(TextUtil.parse(
            "&#29CF92✉ &8| &#29CF6A&lᴘ&#29CF70&lᴏ&#29CF76&lᴛ&#29CF7C&lᴡ&#29CF83&lɪ&#29CF89&lᴇ&#29CF8F&lʀ&#29CF8F&lᴅ&#29CF89&lᴢ &#29CF83&lᴋ&#29CF7C&lᴜ&#29CF76&lᴘ&#29CF70&lɴ&#29CF6A&lᴏ"))
        .rows(3)
        .disableAllInteractions()
        .create();

    gui.getFiller()
        .fill(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());

    final MarketSeller seller = offer.getSeller();
    final ItemStack clone = item.clone();
    gui.setItem(List.of(0, 1, 2, 9, 10, 11, 18, 19, 20),
        FlameItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE)
            .name("&aPotwierdź kupno")
            .asGuiItem(event -> {

              final double price = offer.getPrice().doubleValue();
              if (economyFacade.getBalance(player) < price) {
                TitleUtil.title(player, "&cBłąd!", "&cNie posiadasz tyle pieniędzy!", 20, 40, 20);
                player.closeInventory();
                return;
              }

              final boolean exists = marketOfferRedisStorage.exists(offer.getOfferId());
              if (exists) {

                economyFacade.deposit(seller.getUniqueId(), price);
                economyFacade.withdraw(player.getUniqueId(), price);

                InventoryUtil.addItem(player, clone);
                TitleUtil.title(player, "&2Sukces!", "&aPomyślnie zakupiono ten przedmiot!", 20,
                    40, 20);

                networkMessageService.sendAsync(
                    BukkitMessage.from(
                            "&7Gracz &6{player_name} &7zakupił od Ciebie przedmiot: {item_name} &7za kwotę: &6${price}")
                        .with("player_name", player.getName())
                        .with("item_name", TextUtil.serialize(clone.getItemMeta().displayName()))
                        .with("price",
                            NumberConverter.convertNumber(offer.getPrice().doubleValue()))
                        .apply(),
                    new NetworkMessageFilterBuilder()
                        .targetPlayer(seller.getUniqueId())
                        .build(),
                    NetworkMessageType.CHAT
                );

                redisMessenger.publish(
                    networkServerFacade.getCurrent().getCategory() + "_auctionhouse_actions",
                    new MarketOfferRemove(offer.getOfferId())
                );
                marketOfferRedisStorage.remove(offer.getOfferId());
                marketOfferRepository.delete(offer);

                player.closeInventory();
                open();


              } else {

                TitleUtil.title(player, "&cBłąd!", "&cTen przedmiot został już wykupiony!", 20,
                    40, 20);
                player.closeInventory();

              }


            }));

    gui.setItem(List.of(6, 7, 8, 15, 16, 17, 24, 25, 26),
        FlameItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
            .name("&cAnuluj kupno")
            .asGuiItem(event -> player.closeInventory()));

    gui.setItem(2, 5, FlameItemBuilder.of(clone.clone()).asGuiItem());
    gui.setDefaultClickAction(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    gui.open(player);
  }

}
