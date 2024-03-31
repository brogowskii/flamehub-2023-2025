package io.github.flamehub.essentials.vanish;

import dev.morphia.Morphia;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import org.bukkit.plugin.Plugin;

public final class VanishModule extends BukkitModule {

    private final VanishedEntryCache vanishedEntryCache;
    private final VanishedEntryRepository vanishedEntryRepository;

    public VanishModule(final Plugin plugin, final FlameDispatcher flameDispatcher) {
        super(plugin, flameDispatcher);

        this.vanishedEntryCache = new VanishedEntryCache();
        this.vanishedEntryRepository = new VanishedEntryRepository(
                Morphia.createDatastore(
                        super.databaseConnector.getMongoClient(),
                        super.networkServerCache.getCurrent().getCategory()
                )
        );

        super.registerListeners(
                new VanishListener(
                        super.plugin,
                        super.flameDispatcher,
                        this.vanishedEntryCache,
                        this.vanishedEntryRepository,
                        super.messagesService
                )
        );

        new VanishPlaceholder(this.vanishedEntryCache).register();
        super.commandSet.add(
                new VanishCommand(
                        super.plugin,
                        super.messagesService,
                        this.vanishedEntryCache,
                        this.vanishedEntryRepository
                )
        );


    }

}
