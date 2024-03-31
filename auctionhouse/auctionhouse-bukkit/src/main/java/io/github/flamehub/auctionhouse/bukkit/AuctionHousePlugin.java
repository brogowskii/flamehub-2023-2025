package io.github.flamehub.auctionhouse.bukkit;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.json.gson.JsonGsonConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import io.github.flamehub.auctionhouse.commons.AuctionHouseConfig;
import io.github.flamehub.auctionhouse.bukkit.category.AuctionHouseCategoryConfig;
import io.github.flamehub.auctionhouse.commons.offer.AuctionHouseOfferSorter;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class AuctionHousePlugin extends BukkitPlugin {

    private RedisMessenger redisMessenger;

    private NetworkMessageService networkMessageService;
    private BukkitMessagesService messagesService;
    private Economy economy;

    private AuctionHouseConfig auctionHouseConfig;
    private AuctionHouseCategoryConfig auctionHouseCategoryConfig;

    private AuctionHouseOfferSorter auctionHouseOfferSorter;

    @Override
    public void onEnable() {

        this.redisMessenger = getService(RedisMessenger.class);
        this.messagesService = getService(BukkitMessagesService.class);
        this.economy = getService(Economy.class);

        this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");

        this.auctionHouseConfig = ConfigManager.create(AuctionHouseConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer(), new SerdesBukkit());
            it.withBindFile(this.getDataFolder() + "/config.json");
            it.saveDefaults();
            it.load(true);
        });

        this.auctionHouseCategoryConfig = ConfigManager.create(AuctionHouseCategoryConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer());
            it.withBindFile(this.getDataFolder() + "/categories.json");
            it.saveDefaults();
            it.load(true);
        });

        this.auctionHouseOfferSorter = new AuctionHouseOfferSorter();

        LiteCommandsBukkit.builder()
                .settings(settings -> settings
                        .fallbackPrefix("flamehub-afk-zone")
                        .nativePermissions(false)
                )
                .argument(Location.class, new LocationArgument())
                .argument(Player.class, new PlayerArgument(this.messagesService))
                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new AuctionHouseCommand(flameDispatcher, redisMessenger, networkMessageService, messagesService, auctionHouseConfig, auctionHouseCategoryConfig, economy)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();

    }
}