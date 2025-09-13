package io.github.flamehub.cheque;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.server.NetworkServerContext;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class ChequePlugin extends BukkitModule {

  private Economy economy;
  private ChequeService chequeService;
  private ChequeLogRepository chequeLogRepository;

  @Override
  public void onEnable() {
    super.onEnable();

    economy = getService(Economy.class);
    chequeService = new ChequeService(this, economy);
    chequeLogRepository = new ChequeLogRepository(DatastoreFactory.create(
        databaseConnector.getMongoClient(),
        NetworkServerContext.CURRENT_CATEGORY,
        ChequeLog.class
    ));

    final PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new ChequeListener(chequeService, chequeLogRepository), this);

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("codes")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new ChequeCommand(chequeService, chequeLogRepository, economy)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();



  }
}
