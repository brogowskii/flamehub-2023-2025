package io.github.flamehub.wallet.user;

import dev.morphia.Datastore;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class WalletUserFacadeCreator {

  private WalletUserFacadeCreator() {
  }

  public static WalletUserFacade createWalletUserFacade(
      final FlameDispatcher flameDispatcher,
      final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
      final BukkitMessagesService messagesService,
      final PluginManager pluginManager,
      final Plugin plugin,
      final Datastore datastore,
      final RedisMessenger redisMessenger,
      final RedisService redisService) {

    final WalletUserRepository walletUserRepository = new WalletUserRepository(datastore);
    final WalletUserFactory walletUserFactory = new WalletUserFactory();
    final WalletUserCache walletUserCache = new WalletUserCache(redisMessenger, redisService, walletUserRepository);

    liteCommandsBuilder.context(WalletUser.class, new WalletUserContextual(walletUserCache));
    liteCommandsBuilder.argument(WalletUser.class,
        new WalletUserArgument(walletUserCache, messagesService));

    pluginManager.registerEvents(
        new WalletUserListener(flameDispatcher, pluginManager, walletUserCache,
            walletUserRepository, walletUserFactory), plugin);

    return new WalletUserFacade(walletUserCache, walletUserRepository);
  }

}
