package io.github.flamehub.contest.user;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.user.UserDatabaseListener;
import io.github.flamehub.commons.bukkit.user.event.AsyncPlayerJoinEvent;
import io.github.flamehub.commons.user.UserDatabaseCache;
import io.github.flamehub.commons.user.UserFactory;
import io.github.flamehub.commons.user.UserRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.PluginManager;

final class ContestUserListener extends UserDatabaseListener<ContestUser> {

  private final UserRepository<ContestUser> userRepository;

  public ContestUserListener(
      final FlameDispatcher flameDispatcher,
      final PluginManager pluginManager,
      final UserDatabaseCache<ContestUser> userDatabaseCache,
      final UserRepository<ContestUser> userRepository,
      final UserFactory<ContestUser> userFactory) {
    super(flameDispatcher, pluginManager, userDatabaseCache, userRepository, userFactory);
    this.userRepository = userRepository;
  }

  @EventHandler
  public void onAsyncJoin(final AsyncPlayerJoinEvent event) {
    if (event.getUser() instanceof final ContestUser contestUser) {
      if (contestUser.getContestPoints() < 0) {
        contestUser.setContestPoints(0);
        userRepository.save(contestUser);
      }
    }
  }

}
