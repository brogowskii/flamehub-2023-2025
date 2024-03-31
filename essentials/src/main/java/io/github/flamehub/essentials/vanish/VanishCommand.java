package io.github.flamehub.essentials.vanish;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.essentials.EssentialsConstants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Command(name = "vanish", aliases = "v")
@Permission(EssentialsConstants.VANISH_PERMISSION)
final class VanishCommand {

    private final Plugin plugin;
    private final BukkitMessagesService messagesService;
    private final VanishedEntryCache vanishedEntryCache;
    private final VanishedEntryRepository vanishedEntryRepository;

    VanishCommand(Plugin plugin, BukkitMessagesService messagesService, VanishedEntryCache vanishedEntryCache, VanishedEntryRepository vanishedEntryRepository) {
        this.plugin = plugin;
        this.messagesService = messagesService;
        this.vanishedEntryCache = vanishedEntryCache;
        this.vanishedEntryRepository = vanishedEntryRepository;
    }

    @Execute
    void execute(@Context Player player) {

        if (this.vanishedEntryCache.isVanished(player.getUniqueId())) {
            this.vanishedEntryCache.removeVanished(player.getUniqueId());
            this.vanishedEntryRepository.delete(new VanishedEntry(player.getUniqueId(), player.getName()));
            this.messagesService.sendMessage(player, "vanish.off");

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

        this.vanishedEntryRepository.save(new VanishedEntry(player.getUniqueId(), player.getName()));
        this.vanishedEntryCache.addVanished(player.getUniqueId());
        this.messagesService.sendMessage(player, "vanish.on");

    }



}
