package io.github.flamehub.essentials.user;

import dev.morphia.Morphia;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public final class EssentialsUserModule extends BukkitModule {

    private final EssentialsUserCache essentialsUserCache;
    private final EssentialsUserFactory essentialsUserFactory;
    private final EssentialsUserRepository essentialsUserRepository;

    public EssentialsUserModule(final Plugin plugin, final FlameDispatcher flameDispatcher) {
        super(plugin, flameDispatcher);

        this.essentialsUserRepository = new EssentialsUserRepository(
                Morphia.createDatastore(
                        super.databaseConnector.getMongoClient(),
                        super.networkServerCache.getCurrent().getCategory()
                )
        );
        this.essentialsUserFactory = new EssentialsUserFactory();
        this.essentialsUserCache = new EssentialsUserCache(this.essentialsUserRepository);

        super.registerListeners(
                new EssentialsUserListener(
                        super.flameDispatcher,
                        super.plugin.getServer().getPluginManager(),
                        this.essentialsUserCache,
                        this.essentialsUserRepository,
                        this.essentialsUserFactory
                )
        );
    }

    public EssentialsUser findByUniqueId(@NotNull final UUID uniqueId) {
        return this.essentialsUserCache.findByUniqueId(uniqueId);
    }

    public EssentialsUser findByName(@NotNull final String name) {
        return this.essentialsUserCache.findByName(name);
    }

    public EssentialsUser save(@NotNull final EssentialsUser essentialsUser) {
        return this.essentialsUserRepository.save(essentialsUser);
    }

    public Collection<EssentialsUser> values() {
        return this.essentialsUserCache.values();
    }
}
