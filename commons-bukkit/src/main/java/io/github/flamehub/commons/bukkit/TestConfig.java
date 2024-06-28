package io.github.flamehub.commons.bukkit;

import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@FlameConfigProperties(name = "test.json")
@EnableRemote(collection = "test")
public class TestConfig extends FlameConfig {

    private ItemStack itemStack = FlameItemBuilder.of(Material.STONE)
            .name("&c&lTESTOWY ITEMSTACK")
            .lore("jebac grube kurwy czyli nocka")
            .glow()
            .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
            .flag(ItemFlag.HIDE_ARMOR_TRIM)
            .asItemStack();

    private String test = "test";

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getTest() {
        return test;
    }

    @Override
    public String toString() {
        return "TestConfig{" +
                "itemStack=" + itemStack.toString() +
                '}';
    }
}
