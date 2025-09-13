package io.github.flamehub.report;

import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.context.PlayerOnlyContextProvider;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import io.github.flamehub.commons.bukkit.BukkitModule;
import io.github.flamehub.commons.bukkit.command.argument.PlayerArgument;
import io.github.flamehub.commons.bukkit.command.handler.InvalidUsageHandlerImpl;
import io.github.flamehub.commons.bukkit.command.handler.MissingPermissionHandlerImpl;
import io.github.flamehub.commons.bukkit.network.player.NetworkPlayerArgument;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.server.NetworkServerContext;
import org.bukkit.entity.Player;

public final class ReportPlugin extends BukkitModule {

  private ReportRepository reportRepository;
  private NetworkMessageService networkMessageService;

  @Override
  public void onEnable() {
    super.onEnable();

    networkMessageService = new NetworkMessageService(redisMessenger, "network_messages");
    reportRepository = new ReportRepository(DatastoreFactory.create(
        databaseConnector.getMongoClient(), NetworkServerContext.CURRENT_CATEGORY, Report.class));

    LiteBukkitFactory.builder()
        .settings(settings -> settings
            .fallbackPrefix("report")
            .nativePermissions(false)
        )
        .argument(Player.class, new PlayerArgument(messagesService))
        .argument(NetworkPlayer.class, new NetworkPlayerArgument(messagesService, networkPlayerCache, networkServerFacade))
        .context(Player.class, new PlayerOnlyContextProvider(new MessageRegistry<>()))

        .missingPermission(new MissingPermissionHandlerImpl(messagesService))
        .invalidUsage(new InvalidUsageHandlerImpl(messagesService))

        .commands(LiteCommandsAnnotations.of(
            new ReportCommand(reportRepository, networkServerFacade, networkMessageService, flameDispatcher),
            new ReportAdminCommand(flameDispatcher, reportRepository)
        ))

        .schematicGenerator(SchematicFormat.angleBrackets())
        .build();


  }

  public ReportRepository getReportRepository() {
    return reportRepository;
  }
}
