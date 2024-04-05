package io.github.flamehub.essentials.spawn;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.MongoConfigService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Command(name = "setspawn")
@Permission("server.essentials.command.setspawn")
final class SpawnSetCommand {

    private final MongoConfigService mongoConfigService;
    private final SpawnFacade spawnFacade;

    SpawnSetCommand(final MongoConfigService mongoConfigService, final SpawnFacade spawnFacade) {
        this.mongoConfigService = mongoConfigService;
        this.spawnFacade = spawnFacade;
    }

    @Execute
    void execute(@Context final Player player) {
        final Location spawnLocation = player.getLocation().clone().toCenterLocation();
        this.spawnFacade.setSpawnLocation(spawnLocation);
        this.spawnFacade.saveConfig(this.mongoConfigService);
        player.getWorld().setSpawnLocation(spawnLocation);
        BukkitMessage.from("&aPomyślnie ustawiono nową lokalizacje spawnu!").send(player);
    }

}
