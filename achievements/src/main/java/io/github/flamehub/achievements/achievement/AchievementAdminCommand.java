package io.github.flamehub.achievements.achievement;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.MongoConfigService;
import org.bukkit.command.CommandSender;

@Command(name = "achievementadmin")
@Permission("server.commands.achievementadmin")
public final class AchievementAdminCommand {

    private final MongoConfigService mongoConfigService;
    private final AchievementConfig achievementConfig;
    private final AchievementService achievementService;

    public AchievementAdminCommand(MongoConfigService mongoConfigService, AchievementConfig achievementConfig, AchievementService achievementService) {
        this.mongoConfigService = mongoConfigService;
        this.achievementConfig = achievementConfig;
        this.achievementService = achievementService;
    }

    @Execute(name = "reload")
    void reload(@Context CommandSender sender) {
        try {
            this.mongoConfigService.refresh(AchievementConfig.class, this.achievementConfig);
            BukkitMessage.from("&aPomyślnie przeładowano.").send(sender);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
