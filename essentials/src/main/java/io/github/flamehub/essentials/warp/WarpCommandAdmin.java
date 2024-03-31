package io.github.flamehub.essentials.warp;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.MongoConfigService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "warpadmin")
@Permission("server.essentials.command.warpadmin")
final class WarpCommandAdmin {

    private final MongoConfigService mongoConfigService;
    private final WarpConfig warpConfig;

    WarpCommandAdmin(final MongoConfigService mongoConfigService, final WarpConfig warpConfig) {
        this.mongoConfigService = mongoConfigService;
        this.warpConfig = warpConfig;
    }

    @Execute(name = "create")
    void create(
            @Context final Player player,
            @Arg final String name,
            @Arg final String icon,
            @Arg final int slot,
            @Join final String guiName
    ) {

        final Location location = player.getLocation();
        final Location clone = location.clone().toBlockLocation();
        final Warp warp = new Warp(guiName, Material.valueOf(icon), slot, clone);

        this.warpConfig.putWarp(name.toLowerCase(), warp);
        this.mongoConfigService.save(this.warpConfig);

        BukkitMessage.from("&aPomyślnie stworzyłeś warp o nazwie: &2" + name).send(player);

    }

    @Execute(name = "setlocation")
    void create(@Context final Player player, @Arg String name) {

        name = name.toLowerCase();
        final Warp warp = this.warpConfig.findByName(name);
        if (warp == null) {
            BukkitMessage.from("&cTen warp nie istnieje!").send(player);
            return;
        }

        warp.setLocation(player.getLocation().clone().toCenterLocation());
        this.mongoConfigService.save(this.warpConfig);

        BukkitMessage.from("&aPomyślnie ustawiłeś lokalizacje warp o nazwie &2" + name + " &aw miejscu w którym stoisz.")
                .send(player);

    }

    @Execute(name = "remove")
    void remove(@Context final Player player, @Arg String name) {
        name = name.toLowerCase();
        final Warp warp = this.warpConfig.findByName(name);
        if (warp == null) {
            BukkitMessage.from("&cTen warp nie istnieje!").send(player);
            return;
        }

        this.warpConfig.removeWarp(name);
        this.mongoConfigService.save(this.warpConfig);

        BukkitMessage.from("&aPomyślnie usunąłeś warp o nazwie: &2" + name).send(player);

    }

    @Execute(name = "reload")
    void remove(@Context final CommandSender sender) {

        try {
            this.mongoConfigService.refresh(WarpConfig.class, this.warpConfig);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        BukkitMessage.from("&aPomyślnie przeładowano plik konfiguracyjny!").send(sender);
    }

    @Execute(name = "tp")
    void tp(@Context final CommandSender sender, @Arg final Player player, @Arg final String name) {
        final Warp warp = this.warpConfig.findByName(name);
        if (warp == null) {
            BukkitMessage.from("&cTen warp nie istnieje!").send(player);
            return;
        }

        player.teleport(warp.getLocation().clone());
    }
}
