package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import org.bukkit.entity.Player;

@Command(name = "heal")
@Permission("server.essentials.commands.heal")
final class HealCommand {

    private final BukkitMessagesService messagesService;

    public HealCommand(final BukkitMessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Execute
    void execute(@Context final Player player) {
        heal(player);
        this.messagesService.sendMessage(player, "heal.success");
    }

    @Execute
    @Permission("server.essentials.commands.heal.other")
    void execute(@Context final Player player, @Arg final Player target) {
        heal(target);
        this.messagesService.sendMessage(player, "heal.success.other");

    }

    void heal(final Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);
        player.setFireTicks(0);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }
}
