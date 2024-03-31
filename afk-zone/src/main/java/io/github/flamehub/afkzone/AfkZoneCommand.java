package io.github.flamehub.afkzone;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.config.MongoConfigService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.flamehub.commons.bukkit.text.TextUtil;

@Command(name = "afkzone")
@Permission("server.afkzone.admin")
public final class AfkZoneCommand {

    private final MongoConfigService mongoConfigService;
    private final AfkZoneConfig afkZoneConfig;

    public AfkZoneCommand(MongoConfigService mongoConfigService, AfkZoneConfig afkZoneConfig) {
        this.mongoConfigService = mongoConfigService;
        this.afkZoneConfig = afkZoneConfig;
    }

    @Execute(name = "reload")
    void reload(@Context CommandSender sender) {

        for (AfkZoneReward afkZoneReward : this.afkZoneConfig.getAfkZoneRewards()) {
            afkZoneReward.getBossBarMap().forEach((uuid, bossBar) -> {
                bossBar.removeAll();
                bossBar.setVisible(false);
            });
        }

        try {

            this.mongoConfigService.refresh(AfkZoneConfig.class, this.afkZoneConfig);
            sender.sendMessage(TextUtil.parse("&aSuccessfully reloaded afk-zone config."));

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }

    @Execute(name = "setMin")
    void setMinLocation(@Context Player player) {

        player.sendMessage(TextUtil.parse("&aSuccessfully set min location."));
        this.afkZoneConfig.setMinLocation(player.getLocation().clone());
        this.mongoConfigService.save(afkZoneConfig);

    }

    @Execute(name = "setMax")
    void setMaxLocation(@Context Player player) {

        player.sendMessage(TextUtil.parse("&aSuccessfully set max location."));
        this.afkZoneConfig.setMaxLocation(player.getLocation().clone());
        this.mongoConfigService.save(afkZoneConfig);

    }

}
