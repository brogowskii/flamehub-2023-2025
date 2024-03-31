package io.github.flamehub.essentials;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.LocationArgument;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.network.player.NetworkPlayerArgument;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.essentials.command.CommandModule;
import io.github.flamehub.essentials.command.argument.GameModeArgument;
import io.github.flamehub.essentials.privatemessage.PrivateMessageModule;
import io.github.flamehub.essentials.user.EssentialsUserModule;
import io.github.flamehub.essentials.vanish.VanishModule;
import io.github.flamehub.essentials.warp.WarpModule;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

final class EssentialsModule extends BukkitModule {

    private WarpModule warpModule;
    private VanishModule vanishModule;
    private PrivateMessageModule privateMessageModule;
    private EssentialsUserModule essentialsUserModule;


    public EssentialsModule(final Plugin plugin, final FlameDispatcher flameDispatcher) {
        super(plugin, flameDispatcher);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.essentialsUserModule = new EssentialsUserModule(super.plugin, super.flameDispatcher);
        this.essentialsUserModule.onEnable();

        this.vanishModule = new VanishModule(super.plugin, super.flameDispatcher);
        this.vanishModule.onEnable();

        this.warpModule = new WarpModule(super.plugin, super.flameDispatcher);
        this.vanishModule.onEnable();

        CommandModule commandModule = new CommandModule(super.plugin, super.flameDispatcher);
        commandModule.onEnable();

        this.privateMessageModule = new PrivateMessageModule(super.plugin, super.flameDispatcher, essentialsUserModule);
        this.privateMessageModule.onEnable();

        final LiteCommands<CommandSender> liteCommands = LiteCommandsBukkit.builder()
                .settings(settings -> settings
                        .fallbackPrefix("essentials")
                        .nativePermissions(false)
                )
                .argument(Player.class, new PlayerArgument(super.messagesService))
                .argument(NetworkPlayer.class, new NetworkPlayerArgument(super.messagesService, super.networkPlayerCache, super.networkServerCache))
                .argument(GameMode.class, new GameModeArgument(super.messagesService))
                .argument(Location.class, new LocationArgument())
                .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

                .missingPermission(new MissingPermissionHandlerImpl(super.messagesService))
                .invalidUsage(new InvalidUsageHandlerImpl(super.messagesService))
                .commands(this.vanishModule.getCommandSet(), this.warpModule.getCommandSet(), commandModule.getCommandSet())
                .build();

    }

    @Override
    public void onDisable() {
        this.vanishModule.onDisable();
        this.warpModule.onDisable();
    }
}