package io.github.flamehub.auctionhouse.bukkit;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.flamehub.auctionhouse.commons.AuctionHouseConfig;
import io.github.flamehub.auctionhouse.commons.AuctionHouseSeller;
import io.github.flamehub.auctionhouse.commons.AuctionHouseJsonUtil;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferRemoveRequest;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferRemoveResponse;
import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategory;
import io.github.flamehub.auctionhouse.bukkit.category.AuctionHouseCategoryConfig;
import io.github.flamehub.auctionhouse.commons.category.AuctionHouseCategoryIcon;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferSort;
import io.github.flamehub.auctionhouse.commons.page.*;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.*;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageFilterBuilder;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class AuctionHouseGui {

    private final Map<UUID, Long> cooldown = new ConcurrentHashMap<>();

    private final String prefix;
    private final FlameDispatcher flameDispatcher;
    private final RedisMessenger redisMessenger;

    private final NetworkMessageService networkMessageService;
    private final BukkitMessagesService messagesService;
    private final AuctionHouseConfig auctionHouseConfig;
    private final AuctionHouseCategoryConfig auctionHouseCategoryConfig;

    private final Economy economy;
    private final Player player;

    private AuctionHouseOfferSort sortType = AuctionHouseOfferSort.NONE;
    private AuctionHouseCategory category;

    private int page = 1;

    public AuctionHouseGui(
            FlameDispatcher flameDispatcher, BukkitMessagesService messagesService,
            AuctionHouseConfig auctionHouseConfig,
            RedisMessenger redisMessenger,
            NetworkMessageService networkMessageService, AuctionHouseCategoryConfig auctionHouseCategoryConfig,
            Economy economy,
            Player player
    ) {
        this.flameDispatcher = flameDispatcher;
        this.messagesService = messagesService;
        this.auctionHouseConfig = auctionHouseConfig;
        this.redisMessenger = redisMessenger;
        this.networkMessageService = networkMessageService;
        this.auctionHouseCategoryConfig = auctionHouseCategoryConfig;
        this.economy = economy;
        this.player = player;
        this.prefix = this.auctionHouseConfig.getDatabase() + ".";
    }

    public void open() {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        PaginatedGui gui = Gui.paginated()
                .disableAllInteractions()
                .disableOtherActions()
                .rows(6)
                .pageSize(28)
                .title(Component.text(""))
                .create();

        GuiHelper.fillGui6(gui);

        AuctionHouseOfferPageRequest request = new AuctionHouseOfferPageRequest(page, sortType, category);
        CompletableFuture<AuctionHouseOfferPageResponse> completableFuture = this.redisMessenger.publishFuture(this.auctionHouseConfig.getRedisChannel(), request);
        completableFuture.thenAccept(offerPageResponse -> {
            AuctionHouseOfferPage auctionHouseOfferPage = offerPageResponse.getAuctionHouseOfferPage();
            int maxPage = auctionHouseOfferPage.getMaxPage();

            List<String> auctionHouseOfferPageJsonOffers = auctionHouseOfferPage.getJsonOffers();
            List<AuctionHouseOffer> collect = auctionHouseOfferPageJsonOffers.stream()
                    .map(s -> AuctionHouseJsonUtil.GSON.fromJson(s, AuctionHouseOffer.class))
                    .toList();

            for (AuctionHouseOffer auctionHouseOffer : collect) {
                double offerPrice = auctionHouseOffer.getPrice().doubleValue();
                ItemStack itemStack;
                try {
                    itemStack = AuctionHouseSerializer.deserialize(auctionHouseOffer.getItem().getSerializedItemStack());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                FlameItemBuilder builder = FlameItemBuilder.of(itemStack.clone())
                        .appendLore(this.messagesService.message(prefix + "auctionhouse.lore.info")
                                .with("seller_name", auctionHouseOffer.getSeller().getName())
                                .with("price", NumberConverter.convertNumber(offerPrice))
                                .with("expire", TimeUtil.formatDate(auctionHouseOffer.getExpirationTime()))
                                .apply());

                double buyerBalance = this.economy.getBalance(player);
                if (offerPrice > buyerBalance) {
                    builder.appendLore(this.messagesService.message(prefix + "auctionhouse.lore.not.enough.money")
                            .with("missing_money", NumberConverter.convertNumber(offerPrice - buyerBalance))
                            .apply());
                }
                else {
                    builder.appendLore(this.messagesService.message(prefix + "auctionhouse.lore.can.buy").apply());
                }

                gui.addItem(builder.asGuiItem(event -> {

                    if (auctionHouseOffer.getSeller().getUniqueId().equals(player.getUniqueId())) {
                        this.messagesService.sendMessage(player, prefix + "auctionhouse.error.cannot.buy.own.offer");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        return;
                    }

                    confirmBuy(auctionHouseOffer, itemStack);
                }));
            }

            gui.setItem(6, 5, FlameItemBuilder.of(Material.HOPPER)
                    .name(this.messagesService.getMessage(prefix + "auctionhouse.sorting.name"))
                    .lore(this.messagesService.message(prefix + "auctionhouse.sorting.lore")
                            .with("sort_type", this.sortType.getName())
                            .apply())
                    .asGuiItem(event -> {

                        this.sortType = this.sortType.next();
                        open();

                    }));


            if (page - 1 >= 1) {
                gui.setItem(6, 4, FlameItemBuilder.of(Material.SPECTRAL_ARROW)
                        .name(this.messagesService.getMessage(prefix + "auctionhouse.previous.page.name"))
                        .amount(Math.max(page - 1, 1))
                        .asGuiItem(event -> {

                            Long cooldown = this.cooldown.get(player.getUniqueId());
                            if (cooldown != null && cooldown + 250 > System.currentTimeMillis()) {
                                BukkitMessage.from("&cZwolnij!").send(player);
                                return;
                            }

                            this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                            page = page - 1;
                            open();
                        }));
            }

            if (page < maxPage) {
                gui.setItem(6, 6, FlameItemBuilder.of(Material.SPECTRAL_ARROW)
                        .amount(page + 1)
                        .name(this.messagesService.getMessage(prefix + "auctionhouse.next.page.name"))
                        .asGuiItem(event -> {

                            Long cooldown = this.cooldown.get(player.getUniqueId());
                            if (cooldown != null && cooldown + 250 > System.currentTimeMillis()) {
                                BukkitMessage.from("&cZwolnij!").send(player);
                                return;
                            }

                            this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());

                            page = page + 1;
                            open();
                        }));
            }

            gui.setItem(6, 9, FlameItemBuilder.of(Material.GLOW_ITEM_FRAME)
                    .name(this.messagesService.getMessage(prefix + "auctionhouse.category.name"))
                    .lore(this.messagesService.message(prefix + "auctionhouse.category.lore")
                            .with("category", this.category != null ? this.category.getFriendlyName() : "Wszystko")
                            .apply())
                    .asGuiItem(event -> {
                        categories();
                    }));

            gui.setItem(6, 1, FlameItemBuilder.of(Material.CHEST_MINECART)
                    .name(this.messagesService.getMessage(prefix + "auctionhouse.your.offers.name"))
                    .lore(this.messagesService.getMessages(prefix + "auctionhouse.your.offers.lore"))
                    .asGuiItem(event -> {
                        yourOffers();
                    }));

            gui.setItem(1, 5, FlameItemBuilder.of(Material.BELL)
                    .name(this.messagesService.getMessage(prefix + "auctionhouse.info.name"))
                    .lore(this.messagesService.getMessages(prefix + "auctionhouse.info.lore"))
                    .asGuiItem());

            this.flameDispatcher.dispatch(() -> {
                updateTitle(gui, page, maxPage);
                gui.open(player);
            });


        });

    }

    private void yourOffers() {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        Gui gui = Gui.gui()
                .title(TextUtil.parse("&8&lTwoje oferty"))
                .rows(6)
                .disableAllInteractions()
                .create();

        GuiHelper.fillGui6(gui);

        AuctionHouseSellerOffersRequest request = new AuctionHouseSellerOffersRequest(player.getUniqueId());
        CompletableFuture<AuctionHouseSellerOffersResponse> completableFuture = this.redisMessenger.publishFuture(this.auctionHouseConfig.getRedisChannel(), request);
        completableFuture.thenAccept(offersResponse -> {

            List<String> jsonOffers = offersResponse.getAuctionHouseOfferPage().getJsonOffers();
            List<AuctionHouseOffer> collect = jsonOffers.stream()
                    .map(s -> AuctionHouseJsonUtil.GSON.fromJson(s, AuctionHouseOffer.class))
                    .toList();

            for (AuctionHouseOffer offer : collect) {
                ItemStack itemStack;
                try {
                    itemStack = AuctionHouseSerializer.deserialize(offer.getItem().getSerializedItemStack());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                double price = offer.getPrice().doubleValue();
                FlameItemBuilder builder = FlameItemBuilder.of(itemStack.clone())
                        .appendLore(this.messagesService.message(prefix + "auctionhouse.lore.info")
                                .with("seller_name", offer.getSeller().getName())
                                .with("price", NumberConverter.convertNumber(price))
                                .with("expire", TimeUtil.formatDate(offer.getExpirationTime()))
                                .apply());

                builder.appendLore(this.messagesService.message(prefix + "auctionhouse.lore.remove").apply());

                gui.addItem(builder.asGuiItem(event -> {

                    AuctionHouseOfferRemoveRequest removeRequest = new AuctionHouseOfferRemoveRequest(offer.getOfferId());
                    CompletableFuture<AuctionHouseOfferRemoveResponse> buyRequestResponse = this.redisMessenger.publishFuture(this.auctionHouseConfig.getRedisChannel(), removeRequest);
                    buyRequestResponse.thenAccept(buyResponse -> {

                        boolean success = buyResponse.isSuccess();
                        if (success) {

                            economy.depositPlayer(Bukkit.getOfflinePlayer(offer.getSeller().getUniqueId()), price);
                            economy.withdrawPlayer(player, price);

                            InventoryUtil.addItem(player, itemStack.clone());
                            TitleUtil.title(player, "&2Sukces!", "&aPomyślnie anulowano ofertę!", 20, 40, 20);
                            this.flameDispatcher.dispatch(player::closeInventory);
                        } else {

                            TitleUtil.title(player, "&cBłąd!", "&cTen przedmiot prawdopodobnie został wykupiony.", 20, 40, 20);
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

        });

    }

    private void confirmBuy(AuctionHouseOffer offer, ItemStack item) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        Gui gui = Gui.gui()
                .title(TextUtil.parse("&8&lPotwierdź kupno"))
                .rows(3)
                .disableAllInteractions()
                .create();

        gui.getFiller().fill(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());

        AuctionHouseSeller seller = offer.getSeller();
        ItemStack clone = item.clone();
        gui.setItem(List.of(0,1,2,9,10,11,18,19,20), FlameItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE)
                .name("&aPotwierdź kupno")
                .asGuiItem(event -> {

                    double price = offer.getPrice().doubleValue();
                    if (economy.getBalance(player) < price) {
                        TitleUtil.title(player, "&cBłąd!", "&cNie posiadasz tyle pieniędzy!", 20, 40, 20);
                        player.closeInventory();
                        return;
                    }

                    AuctionHouseOfferRemoveRequest request = new AuctionHouseOfferRemoveRequest(offer.getOfferId());
                    CompletableFuture<AuctionHouseOfferRemoveResponse> buyRequestResponse = this.redisMessenger.publishFuture(this.auctionHouseConfig.getRedisChannel(), request);
                    buyRequestResponse.thenAccept(buyResponse -> {

                        boolean success = buyResponse.isSuccess();
                        if (success) {

                            economy.depositPlayer(Bukkit.getOfflinePlayer(seller.getUniqueId()), price);
                            economy.withdrawPlayer(player, price);

                            InventoryUtil.addItem(player, clone);
                            TitleUtil.title(player, "&2Sukces!", "&aPomyślnie zakupiono ten przedmiot!", 20, 40, 20);
                            this.networkMessageService.send(
                                    this.messagesService.message(prefix + "auctionhouse.buy.success.seller")
                                            .with("player_name", player.getName())
                                            .with("item_name", TextUtil.serialize(clone.getItemMeta().displayName()))
                                            .with("price", NumberConverter.convertNumber(offer.getPrice().doubleValue()))
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
                        }
                        else {

                            TitleUtil.title(player, "&cBłąd!", "&cTen przedmiot został już wykupiony!", 20, 40, 20);
                            this.flameDispatcher.dispatch(player::closeInventory);

                        }

                    });

                }));

        gui.setItem(List.of(6,7,8,15,16,17,24,25,26), FlameItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
                .name("&cAnuluj kupno")
                .asGuiItem(event -> player.closeInventory()));

        gui.setItem(2, 5, FlameItemBuilder.of(clone.clone()).asGuiItem());
        gui.open(player);
    }

    private void updateTitle(BaseGui gui, int currentPageNum, int maxPageNum) {
        gui.updateTitle(TextUtil.legacyColor(this.messagesService.message(prefix + "auctionhouse.title")
                .with("page", String.valueOf(currentPageNum))
                .with("max_page", maxPageNum == 0 ? String.valueOf(1) : String.valueOf(maxPageNum))
                .apply()
                .get(0))
        );
    }

    private void categories() {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        Gui gui = Gui.gui()
                .rows(6)
                .title(TextUtil.parse("&8&lWybierz kategorie"))
                .disableAllInteractions()
                .create();

        GuiHelper.fillGui6(gui);
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
                        }
                        else {
                            this.category = auctionHouseCategory;
                        }

                        page = 1;
                        open();

                    }));
        }

        gui.open(player);

    }

}
