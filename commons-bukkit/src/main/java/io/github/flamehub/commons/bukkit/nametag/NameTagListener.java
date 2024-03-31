package io.github.flamehub.commons.bukkit.nametag;

import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class NameTagListener implements Listener {

    private final NameTagService nameTagService;
    private final FlameDispatcher flameDispatcher;

    private final static LuckPerms LUCK_PERMS = LuckPermsProvider.get();

    public NameTagListener(NameTagService nameTagService, FlameDispatcher flameDispatcher) {
        this.nameTagService = nameTagService;
        this.flameDispatcher = flameDispatcher;

        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            EventBus eventBus = LUCK_PERMS.getEventBus();
            eventBus.subscribe(CommonsPlugin.getInstance(), UserDataRecalculateEvent.class, this::onRecalculate);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        this.flameDispatcher.dispatchAsync(() -> {
            this.nameTagService.create(event.getPlayer());
            this.nameTagService.update(event.getPlayer());
        });
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {

        this.flameDispatcher.dispatchAsync(() -> {
            this.nameTagService.remove(event.getPlayer());
            CommonsPlugin.getInstance().getServer().getPluginManager().callEvent(new NameTagRemoveEvent(event.getPlayer()));
        });
    }

    private void onRecalculate(UserDataRecalculateEvent event) {

        UUID uniqueId = event.getUser().getUniqueId();
        Player player = Bukkit.getPlayer(uniqueId);
        if (player == null) {
            return;
        }

        this.flameDispatcher.dispatchAsync(() -> {
            this.nameTagService.create(player);
            this.nameTagService.update(player);
        });
    }


}
