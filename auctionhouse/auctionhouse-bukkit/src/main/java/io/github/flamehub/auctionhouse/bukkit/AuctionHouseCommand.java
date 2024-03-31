package io.github.flamehub.auctionhouse.bukkit;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.auctionhouse.commons.AuctionHouseConfig;
import io.github.flamehub.auctionhouse.commons.AuctionHouseSeller;
import io.github.flamehub.auctionhouse.commons.AuctionHouseJsonUtil;
import io.github.flamehub.auctionhouse.commons.action.AuctionHouseOfferAdd;
import io.github.flamehub.auctionhouse.bukkit.category.AuctionHouseCategoryConfig;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOffer;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferItem;
import io.github.flamehub.auctionhouse.commons.page.AuctionHouseSellerOffersRequest;
import io.github.flamehub.auctionhouse.commons.page.AuctionHouseSellerOffersResponse;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.util.NumberConverter;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Command(name = "ah", aliases = {"rynek", "bazar", "auctionhouse"})
@Permission("server.commands.auctionhouse")
public final class AuctionHouseCommand {

    private final FlameDispatcher flameDispatcher;
    private final RedisMessenger redisMessenger;

    private final NetworkMessageService networkMessageService;
    private final BukkitMessagesService messagesService;
    private final AuctionHouseConfig auctionHouseConfig;
    private final AuctionHouseCategoryConfig auctionHouseCategoryConfig;
    private final Economy economy;

    public AuctionHouseCommand(FlameDispatcher flameDispatcher, RedisMessenger redisMessenger, NetworkMessageService networkMessageService, BukkitMessagesService messagesService, AuctionHouseConfig auctionHouseConfig, AuctionHouseCategoryConfig auctionHouseCategoryConfig, Economy economy) {
        this.flameDispatcher = flameDispatcher;
        this.redisMessenger = redisMessenger;
        this.networkMessageService = networkMessageService;
        this.messagesService = messagesService;
        this.auctionHouseConfig = auctionHouseConfig;
        this.auctionHouseCategoryConfig = auctionHouseCategoryConfig;
        this.economy = economy;
    }

    @Execute
    void open(@Context Player player) {

        AuctionHouseGui auctionHouseGui = new AuctionHouseGui(this.flameDispatcher, this.messagesService, this.auctionHouseConfig, this.redisMessenger, this.networkMessageService, this.auctionHouseCategoryConfig, this.economy, player);
        auctionHouseGui.open();

    }

    @Execute(name = "wystaw", aliases = {"sell", "sprzedaj"})
    void sell(@Context Player player, @Arg("cena") double price) {

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().toString().contains("SHULKER_BOX")) {
            BukkitMessage.from("&cNie możesz wystawić tego przedmiotu.").send(player);
            return;
        }

        if (price <= 0) {
            BukkitMessage.from("&cCena oferty musi być większa od zera.").send(player);
            return;
        }

        if (price > 1000000000000000.0) {
            BukkitMessage.from("&cNie przesadzaj z tą ceną.").send(player);
            return;
        }

        if (itemInMainHand.getType().isAir()) {
            BukkitMessage.from("&cMusisz trzymać przedmiot w łapce.").send(player);
            return;
        }

        ItemStack clone = itemInMainHand.clone();
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        AuctionHouseSellerOffersRequest request = new AuctionHouseSellerOffersRequest(player.getUniqueId());
        CompletableFuture<AuctionHouseSellerOffersResponse> completableFuture = this.redisMessenger.publishFuture(this.auctionHouseConfig.getRedisChannel(), request);
        completableFuture.thenAccept(offersResponse -> {

            List<String> jsonOffers = offersResponse.getAuctionHouseOfferPage().getJsonOffers();
            int size = jsonOffers.size();

            if (size + 1 > AuctionHouseLimiter.getLimit(player)) {
                BukkitMessage.from("&cNie możesz wystawić więcej przedmiotów na rynku! Sprawdź limity pod komendą: &6/rangi")
                        .send(player);
                player.getInventory().setItemInMainHand(clone);
                return;
            }

            AuctionHouseOffer offer = new AuctionHouseOffer(
                    UUID.randomUUID(),
                    new AuctionHouseSeller(player.getUniqueId(), player.getName()),
                    new AuctionHouseOfferItem(AuctionHouseSerializer.serialize(clone), clone.getType().toString()),
                    BigDecimal.valueOf(price),
                    Instant.now().plus(3, ChronoUnit.DAYS)
            );

            this.redisMessenger.publish(this.auctionHouseConfig.getRedisChannel(), new AuctionHouseOfferAdd(AuctionHouseJsonUtil.GSON.toJson(offer)));
            BukkitMessage.from("&aPomyślnie wystawiono przedmiot na rynek!", "&7Cena przedmiotu: &6$" + NumberConverter.convertNumber(price))
                    .send(player);
        });

    }

    @Execute(name = "reload")
    @Permission("server.commands.auctionhouse.reload")
    void reload(@Context CommandSender sender) {
        this.auctionHouseCategoryConfig.load();
    }

}
