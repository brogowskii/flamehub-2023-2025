package io.github.flamehub.warehouse;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.SerializationUtil;
import io.github.flamehub.warehouse.user.WarehouseUser;
import java.util.List;
import java.util.Map;

import io.github.flamehub.warehouse.user.WarehouseUserCache;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Command(name = "warehouse", aliases = {"magazine", "magazyny", "magazyn"})
@Permission("server.skypvp.commands.magazine")
public final class WarehouseCommand {

  public final static Map<Integer, String> ORDER = Map.of(
      0, "gracz",
      1, "vip",
      2, "svip",
      3, "mvip",
      4, "flame"

  );

  private final static Map<String, WarehouseInfo> MAGAZINE_INFO_MAP = Map.of(
      "gracz",
      new WarehouseInfo(
          "&6&lMagazyn: &f\uE042",
          List.of(
              "",
              " &7Magazyn dostępny od rangi: &f\uE042",
              "",
              "&eKliknij, aby otworzyć magazyn!"),
          Material.GRAY_SHULKER_BOX
      ),
      "vip",
      new WarehouseInfo(
          "&6&lMagazyn: &f\uE051",
          List.of(
              "",
              " &7Magazyn dostępny od rangi: &f\uE051",
              "",
              "&eKliknij, aby otworzyć magazyn!"),
          Material.YELLOW_SHULKER_BOX
      ),
      "svip",
      new WarehouseInfo(
          "&6&lMagazyn: &f\uE04E",
          List.of(
              "",
              " &7Magazyn dostępny od rangi: &f\uE04E",
              "",
              "&eKliknij, aby otworzyć magazyn!"),
          Material.ORANGE_SHULKER_BOX
      ),
      "mvip",
      new WarehouseInfo(
          "&6&lMagazyn: &f\uE049",
          List.of(
              "",
              " &7Magazyn dostępny od rangi: &f\uE049",
              "",
              "&eKliknij, aby otworzyć magazyn!"),
          Material.LIGHT_BLUE_SHULKER_BOX
      ),
      "flame",
      new WarehouseInfo(
          "&6&lMagazyn: &f\uE040",
          List.of(
              "",
              " &7Magazyn dostępny od rangi: &f\uE040",
              "",
              "&eKliknij, aby otworzyć magazyn!"),
          Material.RED_SHULKER_BOX
      )
  );

  private final WarehouseUserCache warehouseUserCache;

  public WarehouseCommand(final WarehouseUserCache warehouseUserCache) {
      this.warehouseUserCache = warehouseUserCache;
  }

  @Execute
  void execute(final @Context Player player) {

    final WarehouseUser warehouseUser = warehouseUserCache.findByKey(player.getUniqueId());

    final Gui gui = Gui.gui()
        .rows(3)
        .title(TextUtil.parse("&8&lMagazyn"))
        .disableAllInteractions()
        .create();

    gui.getFiller().fillBorder(FlameItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE)
        .name(" ")
        .asGuiItem());

    if (warehouseUser.getWarehouseMap().isEmpty()) {
      warehouseUser.getWarehouseMap().put("gracz", new Warehouse("gracz"));
      warehouseUser.getWarehouseMap().put("vip", new Warehouse("vip"));
      warehouseUser.getWarehouseMap().put("svip", new Warehouse("svip"));
      warehouseUser.getWarehouseMap().put("mvip", new Warehouse("mvip"));
      warehouseUser.getWarehouseMap().put("flame", new Warehouse("flame"));
    }

    int slotStart = 11;
    for (int i = 0; i < ORDER.size(); i++) {

      final String s = ORDER.get(i);
      final Warehouse value = warehouseUser.getWarehouseMap().get(s);
      final WarehouseInfo warehouseInfo = MAGAZINE_INFO_MAP.get(value.getId());
      gui.setItem(slotStart++, FlameItemBuilder.of(warehouseInfo.getIcon())
          .name(warehouseInfo.getName())
          .lore(warehouseInfo.getDescription())
          .asGuiItem(inventoryClickEvent -> {

            if (!player.hasPermission("server.skypvp.magazine." + value.getId())) {
              BukkitMessage.from("&cNie masz uprawnień do otwarcia tego magazynu!")
                  .deliver(player);
              return;
            }

            Inventory inventory = value.getInventory();
            if (inventory == null) {
              inventory = Bukkit.createInventory(
                  new WarehouseHolder(value.getId(), player.getUniqueId()),
                  54, TextUtil.parse(warehouseInfo.getName()));
              inventory.setContents(
                  (ItemStack[]) SerializationUtil.deserializeBukkitObject(
                      value.getSerializedContents()));
              value.setInventory(inventory);
            }

            player.closeInventory();
            player.openInventory(inventory);

          }));

    }

    gui.open(player);

  }


}
