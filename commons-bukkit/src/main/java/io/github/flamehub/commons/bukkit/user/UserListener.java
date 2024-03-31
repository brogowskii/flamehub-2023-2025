package io.github.flamehub.commons.bukkit.user;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.user.UserCache;
import io.github.flamehub.commons.user.UserFactory;

public class UserListener<U extends User> implements Listener {

    private final UserCache<U> userCache;
    private final UserFactory<U> userFactory;

    public UserListener(UserCache<U> userCache, UserFactory<U> userFactory) {
        this.userCache = userCache;
        this.userFactory = userFactory;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player source = event.getPlayer();

        U user = this.userCache.findByUniqueId(source.getUniqueId());
        if (user == null) {
            user = this.userFactory.create(source.getUniqueId(), source.getName());
            this.userCache.add(user);
        }

    }
}
