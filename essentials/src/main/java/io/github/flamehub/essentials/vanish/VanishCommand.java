package io.github.flamehub.essentials.vanish;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.essentials.EssentialsConstants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;
import org.eclipse.aether.metadata.Metadata;

@Command(name = "vanish", aliases = "v")
@Permission(EssentialsConstants.VANISH_PERMISSION)
final class VanishCommand {

    private final Plugin plugin;
    private final BukkitMessagesService messagesService;
    private final VanishFacade vanishFacade;

    VanishCommand(
            final Plugin plugin,
            final BukkitMessagesService messagesService,
            final VanishFacade vanishFacade
    ) {
        this.plugin = plugin;
        this.messagesService = messagesService;
        this.vanishFacade = vanishFacade;
    }

    @Execute
    void execute(@Context final Player player) {

        if (this.vanishFacade.isVanished(player.getUniqueId())) {
            this.vanishFacade.removeVanished(player.getUniqueId());
            this.vanishFacade.delete(new VanishedEntry(player.getUniqueId(), player.getName()));
            this.messagesService.sendMessage(player, "vanish.off");

            player.removeMetadata("vanished", plugin);
            for (final Player it : Bukkit.getOnlinePlayers()) {
                it.showPlayer(plugin, player);
            }

            return;
        }


        for (final Player it : Bukkit.getOnlinePlayers()) {
            if (!it.hasPermission(EssentialsConstants.VANISH_PERMISSION)) {
                it.hidePlayer(plugin, player);
            }
        }

        player.setMetadata("vanished", new FixedMetadataValue(plugin, true));
        this.vanishFacade.save(new VanishedEntry(player.getUniqueId(), player.getName()));
        this.vanishFacade.addVanished(player.getUniqueId());
        this.messagesService.sendMessage(player, "vanish.on");

    }



}
