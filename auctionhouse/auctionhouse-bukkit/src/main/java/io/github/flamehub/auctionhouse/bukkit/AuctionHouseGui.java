package io.github.flamehub.auctionhouse.bukkit;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.auctionhouse.bukkit.category.AuctionHouseCategoryConfig;
import io.github.flamehub.auctionhouse.bukkit.offer.AuctionHouseOfferCache;
import io.github.flamehub.auctionhouse.bukkit.offer.AuctionHouseOfferDto;
import io.github.flamehub.auctionhouse.bukkit.offer.AuctionHouseOfferPageSorter;
import io.github.flamehub.auctionhouse.bukkit.offer.AuctionHouseOfferSorter;
import io.github.flamehub.auctionhouse.commons.AuctionHouseConfig;
import io.github.flamehub.auctionhouse.commons.AuctionHouseSeller;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferRemoveRequest;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferRemoveResponse;
import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategory;
import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategoryIcon;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferSort;
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
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.economy.EconomyFacade;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class AuctionHouseGui {

  private final Map<UUID, Long> cooldown = new ConcurrentHashMap<>();

  private final FlameDispatcher flameDispatcher;
  private final RedisMessenger redisMessenger;

  private final NetworkMessageService networkMessageService;
  private final AuctionHouseOfferCache auctionHouseOfferCache;
  private final AuctionHouseConfig auctionHouseConfig;
  private final AuctionHouseCategoryConfig auctionHouseCategoryConfig;

  private final EconomyFacade economyFacade;
  private final Player player;
  int page = 1;
  private AuctionHouseOfferPageSorter pageSorter = new AuctionHouseOfferPageSorter();
  private AuctionHouseOfferSorter sorter = new AuctionHouseOfferSorter();
  private AuctionHouseOfferSort sortType = AuctionHouseOfferSort.NONE;
  private AuctionHouseCategory category;

  public AuctionHouseGui(
      FlameDispatcher flameDispatcher,
      AuctionHouseConfig auctionHouseConfig,
      RedisMessenger redisMessenger,
      NetworkMessageService networkMessageService,
      AuctionHouseOfferCache auctionHouseOfferCache,
      AuctionHouseCategoryConfig auctionHouseCategoryConfig,
      EconomyFacade economyFacade,
      Player player
  ) {
    this.flameDispatcher = flameDispatcher;
    this.auctionHouseConfig = auctionHouseConfig;
    this.redisMessenger = redisMessenger;
    this.networkMessageService = networkMessageService;
    this.auctionHouseOfferCache = auctionHouseOfferCache;
    this.auctionHouseCategoryConfig = auctionHouseCategoryConfig;
    this.economyFacade = economyFacade;
    this.player = player;
  }

  public static void fillGui6(BaseGui gui) {

    gui.getFiller()
        .fillBorder(FlameItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(0, 8, 45, 53),
        FlameItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(Arrays.asList(1, 7, 9, 17, 36, 44, 46, 52),
        FlameItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(49, FlameItemBuilder.of(Material.AIR).asGuiItem());
    gui.setItem(4, FlameItemBuilder.of(Material.AIR).asGuiItem());


  }

  private void updateTitle(BaseGui gui, int currentPageNum, int maxPageNum) {
    gui.updateTitle(TextUtil.legacyColor(BukkitMessage.from(
            "&#29ADCF✉ &8| &#29ADCF&lʀ&#29AACF&lʏ&#29A7CF&lɴ&#29A4CF&lᴇ&#29A1CF&lᴋ &8(&f{page}&8/&7{max_page}&8)")
        .with("page", currentPageNum)
        .with("max_page", maxPageNum == 0 ? 1 : maxPageNum)
        .applyFirst())
    );
  }

  boolean isOnCooldown() {
    Long cooldown = this.cooldown.get(player.getUniqueId());
    if (cooldown != null && cooldown + 250 > System.currentTimeMillis()) {
      BukkitMessage.from("&cZwolnij!").send(player);
      return true;
    }

    this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());
    return false;
  }

  public void open() {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    Gui gui = Gui.gui()
        .disableAllInteractions()
        .disableOtherActions()
        .rows(6)
        .title(Component.text(""))
        .create();

    fillGui6(gui);

    List<AuctionHouseOfferDto> values = new ArrayList<>(this.auctionHouseOfferCache.values());
    values = values.stream()
        .filter(offer -> offer.getExpirationTime().toEpochMilli() > System.currentTimeMillis())
        .toList();
    values = this.sorter.sorted(category, sortType, values);

    Map<Integer, List<AuctionHouseOfferDto>> offersByPageMap = this.pageSorter.offersByPageMap(
        values);
    List<AuctionHouseOfferDto> auctionHouseOffers = offersByPageMap.get(page);
    if (auctionHouseOffers == null) {
      auctionHouseOffers = new ArrayList<>();
    }

    int maxPage = (int) Math.ceil((double) values.size() / 28);
    for (AuctionHouseOfferDto auctionHouseOffer : auctionHouseOffers) {
      double offerPrice = auctionHouseOffer.getPrice().doubleValue();
      ItemStack itemStack = auctionHouseOffer.getItemStack();

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
              .with("seller_name", auctionHouseOffer.getSeller().getName())
              .with("price", NumberConverter.convertNumber(offerPrice))
              .with("expire", TimeUtil.formatDate(auctionHouseOffer.getExpirationTime()))
              .apply());

      double buyerBalance = this.economyFacade.getBalance(player);
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

        if (auctionHouseOffer.getSeller().getUniqueId().equals(player.getUniqueId())) {
          BukkitMessage.from("&#DA0000☹ &cNie możesz kupić własnej oferty!").send(player);
          player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
          return;
        }

        confirmBuy(auctionHouseOffer, itemStack);
      }));
    }

    gui.setItem(6, 5, FlameItemBuilder.of(Material.HOPPER)
        .name(
            "&#29ADCF♻ &8| &#29ADCF&ls&#29A9CF&lᴏ&#29A4CF&lʀ&#29A0CF&lᴛ&#299BCF&lᴏ&#299BCF&lᴡ&#29A0CF&lᴀ&#29A4CF&lɴ&#29A9CF&lɪ&#29ADCF&lᴇ")
        .lore(BukkitMessage.from(
                "",
                " {NONE}",
                " {ASCENDING}",
                " {DESCENDING}",
                " {NEWEST}",
                " {OLDEST}",
                "",
                "&bKliknij, aby zmienić filtr sortujący."
            )
            .with("none", sortType == AuctionHouseOfferSort.NONE ? "&2➤ &aBrak sortowania"
                : "&4➤ &cBrak sortowania")
            .with("ascending",
                sortType == AuctionHouseOfferSort.ASCENDING_PRICE ? "&2➤ &aCena rosnąco"
                    : "&4➤ &cCena rosnąco")
            .with("descending",
                sortType == AuctionHouseOfferSort.DESCENDING_PRICE ? "&2➤ &aCena malejąco"
                    : "&4➤ &cCena malejąco")
            .with("newest",
                sortType == AuctionHouseOfferSort.NEWEST ? "&2➤ &aNajnowsze" : "&4➤ &cNajnowsze")
            .with("oldest",
                sortType == AuctionHouseOfferSort.OLDEST ? "&2➤ &aNajstarsze" : "&4➤ &cNajstarsze")
            .apply())
        .asGuiItem(event -> {

          if (isOnCooldown()) {
            return;
          }

          this.sortType = this.sortType.next();
          open();

        }));

    if (page - 1 >= 1) {
      gui.setItem(6, 4, FlameItemBuilder.of(
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
      gui.setItem(6, 6, FlameItemBuilder.of(
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

    gui.setItem(1, 3, FlameItemBuilder.of(Material.ITEM_FRAME)
        .name(
            "&#29ADCF\uD83D\uDD31 &8| &#29ADCF&lᴡ&#29AACF&lʏ&#29A8CF&lʙ&#29A5CF&lɪ&#29A2CF&lᴇ&#29A0CF&lʀ&#299DCF&lᴢ &#299ACF&lᴋ&#299ACF&lᴀ&#299DCF&lᴛ&#29A0CF&lᴇ&#29A2CF&lɢ&#29A5CF&lᴏ&#29A8CF&lʀ&#29AACF&lɪ&#29ADCF&lᴇ")
        .lore(BukkitMessage.from("", "&bKliknij, aby wybrać kategorię.")
            .with("category", this.category != null ? this.category.getFriendlyName() : "Wszystko")
            .apply())
        .asGuiItem(event -> categories()));

    gui.setItem(1, 7, FlameItemBuilder.of(Material.CHEST_MINECART)
        .name(
            "&#29ADCF✉ &8| &#29ADCF&lᴛ&#29A9CF&lᴡ&#29A5CF&lᴏ&#29A1CF&lᴊ&#299DCF&lᴇ &#2999CF&lᴏ&#299DCF&lғ&#29A1CF&lᴇ&#29A5CF&lʀ&#29A9CF&lᴛ&#29ADCF&lʏ")
        .lore(
            "",
            "&bKliknij, aby zobaczyć swoje oferty."
        )
        .asGuiItem(event -> yourOffers()));

    gui.setItem(1, 5, FlameItemBuilder.of(Material.WARPED_SIGN)
        .name("")
        .lore(
            " &#fba90bW&#fbac0di&#fbb00ft&#fbb311a&#fbb713j &#fbba15n&#fcbe18a &#fcc11aR&#fcc51cy&#fcc81en&#fccc20k&#fccf22u &#fcd224S&#fcd626e&#fcd928r&#fcdd2aw&#fce02ce&#fde42fr&#fde731o&#fdeb33w&#fdee35y&#fdf237m&#fdf539!",
            " &fAby wystawić przedmiot użyj: &#fba90b/&#fbac0dr&#fbaf0fy&#fbb311n&#fbb613e&#fbb915k &#fcbc17w&#fcbf18y&#fcc21as&#fcc61ct&#fcc91ea&#fccc20w &#fccf22<&#fcd224c&#fcd526e&#fcd928n&#fcdc2aa&#fcdf2c>",
            "",
            " &fLimity &#fcc11awystawionych przedmiotów &fdla rang:",
            " &8× &7Gracz &8- &f3 oferty",
            " &8× &eVIP &8- &f5 ofert",
            " &8× &6SVIP &8- &f10 ofert",
            " &8× &bMVIP &8- &f15 ofert",
            " &8× &cFLAME &8- &f20 ofert",
            ""
        )
        .asGuiItem());

    updateTitle(gui, page, maxPage);
    gui.open(player);

  }

  private void yourOffers() {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    Gui gui = Gui.gui()
        .title(TextUtil.parse(
            "&#29ADCF✉ &8| &#29ADCF&lᴛ&#29A9CF&lᴡ&#29A5CF&lᴏ&#29A1CF&lᴊ&#299DCF&lᴇ &#2999CF&lᴏ&#299DCF&lғ&#29A1CF&lᴇ&#29A5CF&lʀ&#29A9CF&lᴛ&#29ADCF&lʏ"))
        .rows(6)
        .disableAllInteractions()
        .create();

    fillGui6(gui);

    List<AuctionHouseOfferDto> auctionHouseOffers = this.auctionHouseOfferCache.values().stream()
        .filter(offer -> offer.getSeller().getUniqueId().equals(player.getUniqueId()))
        .toList();

    for (AuctionHouseOfferDto offer : auctionHouseOffers) {

      double price = offer.getPrice().doubleValue();
      ItemStack itemStack = offer.getItemStack();
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
              .with("seller_name", offer.getSeller().getName())
              .with("price", NumberConverter.convertNumber(price))
              .with("expire", TimeUtil.formatDate(offer.getExpirationTime()))
              .apply());

      builder.appendLore("&6⚠ &eKliknij tutaj, aby &6wycofać &eofertę z rynku.");

      gui.addItem(builder.asGuiItem(event -> {

        AuctionHouseOfferRemoveRequest removeRequest = new AuctionHouseOfferRemoveRequest(
            offer.getOfferId());
        CompletableFuture<AuctionHouseOfferRemoveResponse> removeRequestResponse = this.redisMessenger.publishFuture(
            this.auctionHouseConfig.getMasterChannel(), removeRequest);
        removeRequestResponse.thenAccept(buyResponse -> {

          boolean success = buyResponse.isSuccess();
          if (success) {

            InventoryUtil.addItem(player, itemStack.clone());
            TitleUtil.title(player, "&2Sukces!", "&aPomyślnie anulowano ofertę!", 20, 40, 20);
            this.flameDispatcher.dispatch(player::closeInventory);
          } else {

            TitleUtil.title(player, "&cBłąd!", "&cTen przedmiot prawdopodobnie został wykupiony.",
                20, 40, 20);
            this.flameDispatcher.dispatch(player::closeInventory);

          }
        });

      }));

    }

    gui.setItem(6, 5, FlameItemBuilder.of(Material.RED_CONCRETE)
        .name("&c&lPowrót")
        .lore(
            "",
            " &7Kliknij, aby powrócić na poprzednią stronę.",
            ""
        )
        .asGuiItem(event -> {
          open();
        }));

    this.flameDispatcher.dispatch(() -> gui.open(player));

  }

  private void confirmBuy(AuctionHouseOffer offer, ItemStack item) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    Gui gui = Gui.gui()
        .title(TextUtil.parse(
            "&#29CF92✉ &8| &#29CF6A&lᴘ&#29CF70&lᴏ&#29CF76&lᴛ&#29CF7C&lᴡ&#29CF83&lɪ&#29CF89&lᴇ&#29CF8F&lʀ&#29CF8F&lᴅ&#29CF89&lᴢ &#29CF83&lᴋ&#29CF7C&lᴜ&#29CF76&lᴘ&#29CF70&lɴ&#29CF6A&lᴏ"))
        .rows(3)
        .disableAllInteractions()
        .create();

    gui.getFiller()
        .fill(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());

    AuctionHouseSeller seller = offer.getSeller();
    ItemStack clone = item.clone();
    gui.setItem(List.of(0, 1, 2, 9, 10, 11, 18, 19, 20),
        FlameItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE)
            .name("&aPotwierdź kupno")
            .asGuiItem(event -> {

              double price = offer.getPrice().doubleValue();
              if (economyFacade.getBalance(player) < price) {
                TitleUtil.title(player, "&cBłąd!", "&cNie posiadasz tyle pieniędzy!", 20, 40, 20);
                player.closeInventory();
                return;
              }

              AuctionHouseOfferRemoveRequest request = new AuctionHouseOfferRemoveRequest(
                  offer.getOfferId());
              CompletableFuture<AuctionHouseOfferRemoveResponse> buyRequestResponse = this.redisMessenger.publishFuture(
                  this.auctionHouseConfig.getMasterChannel(), request);
              buyRequestResponse.thenAccept(buyResponse -> {

                boolean success = buyResponse.isSuccess();
                if (success) {

                  economyFacade.deposit(seller.getUniqueId(), price);
                  economyFacade.withdraw(player.getUniqueId(), price);

                  InventoryUtil.addItem(player, clone);
                  TitleUtil.title(player, "&2Sukces!", "&aPomyślnie zakupiono ten przedmiot!", 20,
                      40, 20);
                  this.networkMessageService.send(
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
                  this.flameDispatcher.dispatch(() -> {
                    player.closeInventory();
                    open();
                  });
                } else {

                  TitleUtil.title(player, "&cBłąd!", "&cTen przedmiot został już wykupiony!", 20,
                      40, 20);
                  this.flameDispatcher.dispatch(player::closeInventory);

                }

              });

            }));

    gui.setItem(List.of(6, 7, 8, 15, 16, 17, 24, 25, 26),
        FlameItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
            .name("&cAnuluj kupno")
            .asGuiItem(event -> player.closeInventory()));

    gui.setItem(2, 5, FlameItemBuilder.of(clone.clone()).asGuiItem());
    gui.open(player);
  }

  private void categories() {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    Gui gui = Gui.gui()
        .rows(6)
        .title(TextUtil.parse(
            "&#29ADCF✉ &8| &#29ADCF&lᴋ&#29A8CF&lᴀ&#29A3CF&lᴛ&#299ECF&lᴇ&#2999CF&lɢ&#299ECF&lᴏ&#29A3CF&lʀ&#29A8CF&lɪ&#29ADCF&lᴇ"))
        .disableAllInteractions()
        .create();

    fillGui6(gui);
    gui.setItem(6, 5, FlameItemBuilder.of(Material.RED_CONCRETE)
        .name("&c&lPowrót")
        .lore(
            "",
            " &7Kliknij, aby powrócić na poprzednią stronę.",
            ""
        )
        .asGuiItem(event -> {
          open();
        }));

    List<AuctionHouseCategory> auctionHouseCategories = this.auctionHouseCategoryConfig.getAuctionHouseCategories();
    for (AuctionHouseCategory auctionHouseCategory : auctionHouseCategories) {
      AuctionHouseCategoryIcon icon = auctionHouseCategory.getIcon();
      gui.setItem(icon.getSlot(), FlameItemBuilder.of(Material.valueOf(icon.getMaterial()))
          .name(icon.getName())
          .lore(icon.getLore())
          .asGuiItem(event -> {

            if (auctionHouseCategory.getId().equals("all")) {
              this.category = null;
            } else {
              this.category = auctionHouseCategory;
            }

            page = 1;
            open();

          }));
    }

    gui.open(player);

  }

}
