package io.github.flamehub.crates;

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
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.argument.WorldArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public final class CratesPlugin extends BukkitPlugin {

    private CratesConfig cratesConfig;
    private BukkitMessagesService messagesService;

    @Override
    public void onEnable() {

        this.messagesService = getService(BukkitMessagesService.class);

        this.cratesConfig = ConfigManager.create(CratesConfig.class, (it) -> {
            it.withConfigurer(new JsonGsonConfigurer());
            it.withSerdesPack(registry -> {

                registry.register(new SerdesBukkit());
                registry.registerExclusive(ItemStack.class, new ItemStackSerializer(true));

            });
            it.withBindFile(this.getDataFolder() + "/crates.json");
            it.saveDefaults();
            it.load(true);
        });

        setupListeners();
        setupCommands();

    }

    void setupListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new CrateListener(this, this.cratesConfig, messagesService), this);
    }

    void setupCommands() {
        LiteBukkitFactory.builder()
                .settings(settings -> settings
                        .fallbackPrefix("flamehub-commons")
                        .nativePermissions(false)
                )
                .argument(Location.class, new LocationArgument())
                .argument(World.class, new WorldArgument())
                .argument(Player.class, new PlayerArgument(this.messagesService))
                .argument(Crate.class, new CrateArgument(this.cratesConfig))

                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(this.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(this.messagesService))

                .commands(LiteCommandsAnnotations.of(
                        new CrateCommand(this.cratesConfig)
                ))

                .schematicGenerator(SchematicFormat.angleBrackets())
                .build();
    }

}