package io.github.flamehub.scratch;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.commons.bukkit.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "scratchadmin")
@Permission("server.boxpvp.commands.scratchadmin")
public final class ScratchAdminCommand {

    private final ScratchConfig scratchConfig;

    public ScratchAdminCommand(ScratchConfig scratchConfig) {
        this.scratchConfig = scratchConfig;
    }

    @Execute(name = "setscratchitem")
    void setItem(@Context Player player) {

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().isAir()) {
            return;
        }

        this.scratchConfig.setScratchCardItem(itemInMainHand.clone());
        this.scratchConfig.save();

    }

    @Execute(name = "getscratchitem")
    void get(@Context Player player) {
        ItemStack scratchCardItem = this.scratchConfig.getScratchCardItem();
        if (scratchCardItem == null) {
            player.sendMessage("item == null");
            return;
        }

        InventoryUtil.addItem(player, scratchCardItem.clone());
    }

    @Execute(name = "give")
    void give(@Context CommandSender sender, @Arg Player target, @Arg int amount) {
        ItemStack scratchCardItem = this.scratchConfig.getScratchCardItem();
        if (scratchCardItem == null) {
            sender.sendMessage("item == null");
            return;
        }

        ItemStack clone = scratchCardItem.clone();
        clone.setAmount(amount);
        InventoryUtil.addItem(target.getPlayer(), clone);
    }

    @Execute(name = "setdropitem")
    void addDropItem(@Context Player player, @Arg int slot, @Arg double chance) {

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().isAir()) {
            return;
        }

        this.scratchConfig.getScratchCardDrops().put(slot, new ScratchDrop(itemInMainHand.clone(), chance));
        this.scratchConfig.save();

    }

    @Execute(name = "removeitem")
    void removeDropItem(@Context Player player, @Arg int slot) {

        this.scratchConfig.getScratchCardDrops().remove(slot);
        this.scratchConfig.save();

    }

    @Execute(name = "setscratchpreviewloc")
    void setScratchPreviewLoc(@Context Player player) {
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            return;
        }

        this.scratchConfig.setPreviewLocation(targetBlock.getLocation());
        this.scratchConfig.save();
    }

    @Execute(name = "switchstatus")
    void switchStatus(@Context CommandSender sender) {

        this.scratchConfig.setEnabled(!this.scratchConfig.isEnabled());
        this.scratchConfig.save();

        TextBuilder.builder()
                .text("&7Zmieniono status zdrapek na: " + (this.scratchConfig.isEnabled() ? "&awłączony" : "&cwyłączony"))
                .send(sender);

    }

    @Execute(name = "giveall")
    void giveAll(@Context Player sender) {
        if(sender.getName().equals("opalkamarcin")) {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.getInventory().addItem(this.scratchConfig.getScratchCardItem().clone());
            }
        }
    }

    @Execute(name = "reload")
    void reload(@Context CommandSender sender) {

        this.scratchConfig.load();
        sender.sendMessage("przeladowano");

    }

}
