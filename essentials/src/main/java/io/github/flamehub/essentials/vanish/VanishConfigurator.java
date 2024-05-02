package io.github.flamehub.essentials.vanish;

import com.mongodb.client.MongoClient;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import io.github.flamehub.commons.bukkit.BukkitConfigurator;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.database.DatastoreFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class VanishConfigurator extends BukkitConfigurator {

    public VanishFacade vanishFacade(
            final LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> liteCommandsBuilder,
            final MongoClient mongoClient,
            final Plugin plugin,
            final String databaseName,
            final FlameDispatcher flameDispatcher,
            final BukkitMessagesService messagesService
    ) {

        final VanishedEntryRepository vanishedEntryRepository = new VanishedEntryRepository(DatastoreFactory.create(mongoClient, databaseName, VanishedEntry.class));
        final VanishFacade vanishFacade = new VanishFacade(vanishedEntryRepository);

        registerListeners(
                plugin,
                new VanishListener(plugin, flameDispatcher, vanishFacade, messagesService)
        );

        liteCommandsBuilder.commands(LiteCommandsAnnotations.of(
                new VanishCommand(plugin, messagesService, vanishFacade)
        ));

        new VanishPlaceholder(vanishFacade).register();
        return vanishFacade;
    }

}
