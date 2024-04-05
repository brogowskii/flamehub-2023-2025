package io.github.flamehub.scratch;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.json.gson.JsonGsonConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import eu.okaeri.configs.yaml.bukkit.serdes.serializer.ItemStackSerializer;
import io.github.flamehub.commons.bukkit.BukkitPlugin;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public final class ScratchPlugin extends BukkitPlugin {

    private RedisMessenger redisMessenger;

    private NetworkMessageService networkMessageService;
    private BukkitMessagesService messagesService;
    private ScratchConfig scratchConfig;

    @Override
    public void onEnable() {
        this.redisMessenger = getService(RedisMessenger.class);
        this.messagesService = getService(BukkitMessagesService.class);
        this.networkMessageService = new NetworkMessageService(this.redisMessenger, "network_messages");

        this.scratchConfig = ConfigManager.create(ScratchConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer());
            it.withSerdesPack(registry -> {

                registry.register(new SerdesBukkit());
                registry.registerExclusive(ItemStack.class, new ItemStackSerializer(true));

            });
            it.withBindFile(this.getDataFolder() + "/scratchConfig.json");
            it.saveDefaults();
            it.load(true);
        });

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new ScratchListener(networkMessageService, this.scratchConfig), this);

        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("flamehub-scratch")
                        .nativePermissions(false)
                )
                .argument(Location.class, new LocationArgument())
                .argument(World.class, new WorldArgument())

                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new ScratchCommand(this.scratchConfig),
                        new ScratchAdminCommand(this.scratchConfig)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();

    }
}
