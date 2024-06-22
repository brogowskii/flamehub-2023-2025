package io.github.flamehub.kits.kit.management;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.config.MongoConfigService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.kits.KitsConfig;
import io.github.flamehub.kits.kit.Kit;

import java.util.ArrayList;

@Command(name = "kitmanagement", aliases = "kitmanage")
@Permission("server.commands.kitmanage")
public final class KitManagementCommand {

    private final MongoConfigService mongoConfigService;
    private final KitsConfig kitsConfig;

    public KitManagementCommand(MongoConfigService mongoConfigService, KitsConfig kitsConfig) {
        this.mongoConfigService = mongoConfigService;
        this.kitsConfig = kitsConfig;
    }

    @Execute(name = "reload")
    void reload(@Context CommandSender sender) {
        try {
            this.mongoConfigService.refresh(KitsConfig.class, this.kitsConfig);
            BukkitMessage.from("&aPomyślnie przedładowano konfiguracje pluginu &2kits&a!").send(sender);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Execute(name = "create")
    void create(@Context CommandSender sender, @Arg String name, @Arg String permission, @Arg String icon, @Arg String cooldown, @Arg int slot, @Join String title) {

        Kit kit = new Kit(name, new ArrayList<>(), title, permission, title, slot,
                FlameItemBuilder.of(Material.valueOf(icon)).asItemStack(),
                new ArrayList<>(),
                cooldown,
                true
        );

        this.kitsConfig.getKits().add(kit);
        this.mongoConfigService.save(this.kitsConfig);
        BukkitMessage.from("&aPomyślnie stworzono zestaw o nazwie: &2" + name)
                .send(sender);
    }

    @Execute(name = "edit")
    void edit(@Context Player player, @Arg Kit kit) {

        Inventory inventory = Bukkit.createInventory(player, 54, TextUtil.parse("&8Edytor zestawu: " + kit.getName()));
        for (ItemStack itemStack : kit.getItems()) {
            inventory.addItem(itemStack);
        }

        player.openInventory(inventory);
    }

    @Execute(name = "switch", aliases = "switchstatus")
    void switchStatus(@Context CommandSender sender, @Arg Kit kit) {

        kit.setEnable(!kit.isEnable());
        this.mongoConfigService.save(this.kitsConfig);
        BukkitMessage.from("&7Pomyślnie zmieniono status zestawu &2" + kit.getName() + " &7na: " + (kit.isEnable() ? "&awłączony" : "&cwyłączony") + "&7.")
                .send(sender);
    }

    @Execute(name = "cooldown", aliases = "setcooldown")
    void cooldown(@Context CommandSender sender, @Arg Kit kit, @Arg String cooldown) {
        kit.setCooldown(cooldown);
        this.mongoConfigService.save(this.kitsConfig);
        BukkitMessage.from("&7Pomyślnie ustawiono cooldown zestawu &2" + kit.getName() + " &7na: &f" + cooldown)
                .send(sender);
    }

    @Execute(name = "icon", aliases = "seticon")
    void icon(@Context CommandSender sender, @Arg Kit kit, @Arg String icon) {
        try {
            kit.setIcon(FlameItemBuilder.of(Material.valueOf(icon)).asItemStack());
        }
        catch (Exception e) {
            BukkitMessage.from("&cPodałeś złą nazwe materiału!").send(sender);
            return;
        }

        this.mongoConfigService.save(this.kitsConfig);
        BukkitMessage.from("&7Pomyślnie ustawiono ikonke zestawu &a" + kit.getName() + " &7na: &f" + icon)
                .send(sender);
    }

    @Execute(name = "permission", aliases = "setpermission")
    void permission(@Context CommandSender sender, @Arg Kit kit, @Arg String permission) {
        kit.setPermission(permission);
        this.mongoConfigService.save(this.kitsConfig);
        BukkitMessage.from("&7Pomyślnie ustawiono permisje zestawu &a" + kit.getName() + " &7na: &f" + permission)
                .send(sender);
    }

    @Execute(name = "title", aliases = "settitle")
    void title(@Context CommandSender sender, @Arg Kit kit, @Join String title) {
        kit.setTitle(title);
        this.mongoConfigService.save(this.kitsConfig);
        BukkitMessage.from("&7Pomyślnie ustawiono title zestawu &a" + kit.getName() + " &7na: &f" + title)
                .send(sender);
    }

    @Execute(name = "slot", aliases = "setslot")
    void slot(@Context CommandSender sender, @Arg Kit kit, @Arg int slot) {
        kit.setSlot(slot);
        this.mongoConfigService.save(this.kitsConfig);
        BukkitMessage.from("&7Pomyślnie ustawiono slot zestawu &a" + kit.getName() + " &7na: &f" + slot)
                .send(sender);
    }

}
