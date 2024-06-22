package io.github.flamehub.commons.bukkit.user;

import io.github.flamehub.commons.bukkit.user.event.UserQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.event.AsyncPlayerJoinEvent;
import io.github.flamehub.commons.bukkit.user.event.PlayerChangeNameEvent;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;
import io.github.flamehub.commons.user.UserFactory;

public class UserDatabaseListener<U extends User> implements Listener {

    private final FlameDispatcher flameDispatcher;
    private final PluginManager pluginManager;
    private final UserDatabaseCache<U> userDatabaseCache;
    private final UserDatabaseRepository<U> userDatabaseRepository;
    private final UserFactory<U> userFactory;

    public UserDatabaseListener(
            final FlameDispatcher flameDispatcher,
            final PluginManager pluginManager,
            final UserDatabaseCache<U> userDatabaseCache,
            final UserDatabaseRepository<U> userDatabaseRepository,
            final UserFactory<U> userFactory
    ) {
        this.flameDispatcher = flameDispatcher;
        this.pluginManager = pluginManager;
        this.userDatabaseCache = userDatabaseCache;
        this.userDatabaseRepository = userDatabaseRepository;
        this.userFactory = userFactory;
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event) {
        final Player source = event.getPlayer();
        this.flameDispatcher.dispatchAsyncLater(() -> {

            boolean firstJoin = false;
            U user = this.userDatabaseCache.findByUniqueId(source.getUniqueId());
            if (user == null) {
                user = this.userFactory.create(source.getUniqueId(), source.getName());
                this.userDatabaseRepository.save(user);
                firstJoin = true;

            }

            this.userDatabaseCache.add(user);
            if (!user.getName().equalsIgnoreCase(source.getName())) {
                this.userDatabaseCache.updateName(user, source.getName());
                this.userDatabaseRepository.save(user);
                this.pluginManager.callEvent(new PlayerChangeNameEvent(user, user.getName(), source.getName()));
            }

            final AsyncPlayerJoinEvent bukkitPlayerJoinEvent = new AsyncPlayerJoinEvent(source, user, firstJoin);
            this.pluginManager.callEvent(bukkitPlayerJoinEvent);
        }, 10L);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(final PlayerQuitEvent event) {
        final Player source = event.getPlayer();
        final U user = this.userDatabaseCache.findByUniqueId(source.getUniqueId());
        if (user == null) {
            return;
        }

        UserQuitEvent userQuitEvent = new UserQuitEvent(source, user);
        this.pluginManager.callEvent(userQuitEvent);
        this.userDatabaseCache.remove(user);
        this.flameDispatcher.dispatchAsync(() -> this.userDatabaseRepository.save(user));

    }
}
