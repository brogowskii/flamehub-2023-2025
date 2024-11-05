package io.github.flamehub.tiktok.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.bukkit.user.event.AsyncPlayerJoinEvent;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserDatabaseRepository;
import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.tiktok.TikTokService;
import io.github.flamehub.tiktok.video.TikTokVideo;
import io.github.flamehub.tiktok.video.TikTokVideoWrapper;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.PluginManager;

public final class TikTokUserListener extends UserDatabaseListener<TikTokUser> {

  public TikTokUserListener(
      final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserDatabaseCache<TikTokUser> userDatabaseCache,
      final UserDatabaseRepository<TikTokUser> userDatabaseRepository,
      final UserFactory<TikTokUser> userFactory) {
    super(flameDispatcher, pluginManager, userDatabaseCache, userDatabaseRepository, userFactory);
  }


}
