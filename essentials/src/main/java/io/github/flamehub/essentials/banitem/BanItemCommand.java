package io.github.flamehub.essentials.banitem;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.legacy.config.MongoConfigService;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "banitem")
@Permission("server.essentials.commands.banitem")
final class BanItemCommand {

    private final MongoConfigService mongoConfigService;
    private final BanItemFacade banItemFacade;

    public BanItemCommand(final BanItemFacade banItemFacade, final MongoConfigService mongoConfigService) {
        this.mongoConfigService = mongoConfigService;
        this.banItemFacade = banItemFacade;
    }

    @Execute(name = "addBreak")
    void addBreak(@Context final Player player) {
        final Block targetBlock = player.getTargetBlock(10);
        if (targetBlock.getType().isAir()) {
            return;
        }

        if (this.banItemFacade.getMaterialsBreak().contains(targetBlock.getType())) {
            player.sendMessage("istnieje juz");
            return;
        }

        this.banItemFacade.getMaterialsBreak().add(targetBlock.getType());
        this.banItemFacade.saveConfig(this.mongoConfigService);

        player.sendMessage("dodano");
    }

    @Execute(name = "addPlace")
    void addPlace(@Context final Player player) {
        final Block targetBlock = player.getTargetBlock(10);
        if (targetBlock.getType().isAir()) {
            return;
        }

        if (banItemFacade.getMaterialsPlace().contains(targetBlock.getType())) {
            player.sendMessage("istnieje juz");
            return;
        }

        this.banItemFacade.getMaterialsPlace().add(targetBlock.getType());
        this.banItemFacade.saveConfig(this.mongoConfigService);

        player.sendMessage("dodano");
    }

    @Execute(name = "addCrafting")
    void addCrafting(@Context final Player player) {
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage("trzymaj w lapce item");
            return;
        }

        if (banItemFacade.getCraftings().contains(item.getType())) {
            player.sendMessage("istnieje juz");
            return;
        }

        this.banItemFacade.getCraftings().add(item.getType());
        this.banItemFacade.saveConfig(this.mongoConfigService);

        player.sendMessage("dodano");
    }

    @Execute(name = "reload")
    void reload(@Context final CommandSender sender) {
        try {
            this.banItemFacade.refreshConfig(this.mongoConfigService);
            sender.sendMessage("przeladowano");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
