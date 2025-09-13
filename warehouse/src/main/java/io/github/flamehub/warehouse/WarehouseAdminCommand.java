package io.github.flamehub.warehouse;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.flag.Flag;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import io.github.flamehub.warehouse.user.WarehouseUser;
import io.github.flamehub.warehouse.user.WarehouseUserCache;
import io.github.flamehub.warehouse.user.WarehouseUserRepository;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

@Command(name = "magazineadmin", aliases = {"warehouseadmin"})
@Permission("server.skypvp.commands.magazine.admin")
public final class WarehouseAdminCommand {

  private final NetworkServerFacade networkServerFacade;
  private final NetworkPlayerCache networkPlayerCache;
  private final WarehouseUserCache warehouseUserCache;
  private final WarehouseUserRepository warehouseUserRepository;

  public WarehouseAdminCommand(
      final NetworkServerFacade networkServerFacade,
      final NetworkPlayerCache networkPlayerCache,
      final WarehouseUserCache warehouseUserCache,
      final WarehouseUserRepository warehouseUserRepository) {
    this.networkServerFacade = networkServerFacade;
    this.networkPlayerCache = networkPlayerCache;
    this.warehouseUserCache = warehouseUserCache;
    this.warehouseUserRepository = warehouseUserRepository;
  }

  @Execute(name = "open")
  void open(final @Context Player player, @Arg final String playerName, @Arg final int magazineId) {

    final WarehouseUser warehouseUser = warehouseUserCache.findByName(playerName);
    if (warehouseUser == null) {
      BukkitMessage.from("&cNie znaleziono gracza o nicku &f" + playerName + "&c w bazie danych!")
          .deliver(player);
      return;
    }

    final NetworkPlayer networkPlayer = networkPlayerCache.findByName(playerName);
    final NetworkServer current = networkServerFacade.getCurrent();
    if (Objects.equals(current.getCategory(), networkPlayer.getServerCategory()) &&
        !Objects.equals(current.getName(), networkPlayer.getServer())) {

      BukkitMessage.from(
              "&cGracz &f" + playerName + "&c jest aktualnie na serwerze &f" + networkPlayer.getServer()
                  + "&c!")
          .deliver(player);
      return;
    }

    final Warehouse warehouse = warehouseUser.getWarehouseMap()
        .get(WarehouseCommand.ORDER.get(magazineId));
    if (warehouse == null) {
      BukkitMessage.from(
              "&cGracz &f" + playerName + "&c nie posiada magazynu o id &f" + magazineId + "&c!")
          .deliver(player);
      return;
    }

    if (warehouse.getInventory() == null) {
      BukkitMessage.from("&cMagazyn gracza &f" + playerName + "&c nie został nigdy otwarty!")
          .deliver(player);
      return;
    }

    player.closeInventory();
    player.openInventory(warehouse.getInventory());

  }

  @Execute(name = "scan")
  void exec(@Context final Player player, @Arg final int minAmount,
      @Flag("-i") final boolean customModelData) {
    final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType() == Material.AIR) {
      player.sendMessage("Musisz trzymać item w ręce");
      return;
    }

    if (customModelData) {
      if (itemInMainHand.getItemMeta() == null) {
        player.sendMessage("Item meta nie moze byc nullem");
        return;
      }

      if (!itemInMainHand.getItemMeta().hasCustomModelData()) {
        player.sendMessage("Item meta nie ma custom model data");
        return;
      }
    }

    final Material material = itemInMainHand.getType();
    CompletableFuture.supplyAsync(() -> {
          player.sendMessage("ładuje wszystkie dane graczy....");
          return warehouseUserRepository.loadAll();
        })
        .thenCompose(playerSyncDataList -> {
          player.sendMessage("Dane załadowane, rozpoczynam iterację.");
          player.sendMessage("size: " + playerSyncDataList.size());

          final int chunkSize = 100;
          final List<CompletableFuture<Void>> futures = IntStream
              .range(0, (playerSyncDataList.size() + chunkSize - 1) / chunkSize)
              .mapToObj(i -> {
                final List<WarehouseUser> chunk = playerSyncDataList.subList(i * chunkSize,
                    Math.min(playerSyncDataList.size(), (i + 1) * chunkSize));
                return CompletableFuture.runAsync(
                    () -> processChunk(chunk, player, itemInMainHand, material, customModelData,
                        minAmount));
              })
              .toList();

          return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        })
        .thenAccept(__ -> {
          player.sendMessage("zakonczono skanowanie, lista graczy: ");
        })
        .exceptionally(throwable -> {
          player.sendMessage("Wystąpił błąd podczas skanowania: " + throwable.getMessage());
          throwable.printStackTrace();
          return null;
        });
  }

  private void processChunk(
      final List<WarehouseUser> chunk,
      final Player player,
      final ItemStack itemInMainHand,
      final Material material,
      final boolean customModelData,
      final int minAmount
  ) {
    for (final WarehouseUser warehouseUser : chunk) {

      int total = 0;
      for (final Warehouse warehouse : warehouseUser.getWarehouseMap().values()) {
        if (warehouse.getInventory() == null) {
          continue;
        }

        total += countItems(warehouse.getInventory().getContents(), itemInMainHand, material,
            customModelData);
      }

      if (total > minAmount) {
        player.sendMessage(warehouseUser.getName() + " >> " + total + " szt.");
      }
    }
  }

  private int countItems(
      final ItemStack[] container,
      final ItemStack itemInMainHand,
      final Material material,
      final boolean customModelData
  ) {
    int count = 0;

    for (final ItemStack itemStack : container) {
      if (itemStack == null) {
        continue;
      }

      if (itemStack.getType().toString().contains("SHULKER_BOX")) {
        if (itemStack.hasItemMeta()
            && itemStack.getItemMeta() instanceof final BlockStateMeta blockStateMeta
            && blockStateMeta.getBlockState() instanceof final ShulkerBox shulker) {
          for (final ItemStack content : shulker.getInventory().getContents()) {
            if (content == null) {
              continue;
            }

            if (customModelData) {
              if (!content.hasItemMeta() || !content.getItemMeta().hasCustomModelData()) {
                continue;
              }
              if (itemInMainHand.getItemMeta().getCustomModelData()
                  == content.getItemMeta().getCustomModelData()) {
                count += content.getAmount();
              }
            } else if (content.getType() == material) {
              count += content.getAmount();
            }
          }
        }
      }

      if (customModelData) {
        if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasCustomModelData()) {
          continue;
        }
        if (itemInMainHand.getItemMeta().getCustomModelData()
            == itemStack.getItemMeta().getCustomModelData()) {
          count += itemStack.getAmount();
        }
      } else if (itemStack.getType() == material) {
        count += itemStack.getAmount();
      }
    }

    return count;
  }

}
