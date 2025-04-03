package io.github.flamehub.player.sync.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.flag.Flag;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.util.SerializationUtil;
import io.github.flamehub.player.sync.data.PlayerSyncData;
import io.github.flamehub.player.sync.data.PlayerSyncDataRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

@Command(name = "scanusers")
@Permission("scan")
public final class ScanUsersCommand {

  private final FlameDispatcher flameDispatcher;
  private final PlayerSyncDataRepository playerSyncDataRepository;

  public ScanUsersCommand(FlameDispatcher flameDispatcher,
      PlayerSyncDataRepository playerSyncDataRepository) {
    this.flameDispatcher = flameDispatcher;
    this.playerSyncDataRepository = playerSyncDataRepository;
  }

  @Execute
  void exec(@Context Player player, @Arg int minAmount, @Flag("-i") boolean customModelData) {
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
          return playerSyncDataRepository.loadAll();
        })
        .thenCompose(playerSyncDataList -> {
          player.sendMessage("Dane załadowane, rozpoczynam iterację.");
          player.sendMessage("size: " + playerSyncDataList.size());
          int chunkSize = 100; // Rozmiar każdej części
          List<CompletableFuture<Void>> futures = IntStream.range(0,
                  (playerSyncDataList.size() + chunkSize - 1) / chunkSize)
              .mapToObj(i -> {
                List<PlayerSyncData> chunk = playerSyncDataList.subList(i * chunkSize,
                    Math.min(playerSyncDataList.size(), (i + 1) * chunkSize));
                return CompletableFuture.runAsync(
                    () -> processChunk(chunk, player, itemInMainHand, material, customModelData,
                        minAmount));
              })
              .collect(Collectors.toList());

          return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        })
        .thenAccept(sss -> {
          player.sendMessage("zakonczono skanowanie, lista graczy: ");
        })
        .exceptionally(throwable -> {
          player.sendMessage("Wystąpił błąd podczas skanowania: " + throwable.getMessage());
          throwable.printStackTrace();
          return null;
        });
  }

  private void processChunk(List<PlayerSyncData> chunk, Player player, ItemStack itemInMainHand,
      Material material, boolean customModelData, int minAmount) {
    for (PlayerSyncData playerSyncData : chunk) {
      ItemStack[] inventory;
      ItemStack[] enderChest;
      try {
        enderChest = (ItemStack[]) SerializationUtil.deserializeBukkitObject(
            playerSyncData.getSerializedEnderchest());
        inventory = (ItemStack[]) SerializationUtil.deserializeBukkitObject(
            playerSyncData.getSerializedInventory());
      } catch (Exception e) {
        player.sendMessage("blad: " + e.getMessage());
        continue;
      }

      if (enderChest == null || inventory == null) {
        player.sendMessage("blad: enderChest == null || inventory == null");
        continue;
      }

      int i = 0;
      for (ItemStack itemStack : enderChest) {
        if (itemStack == null) {
          continue;
        }

        if (itemStack.getType().toString().contains("SHULKER_BOX")) {
          if (itemStack.hasItemMeta()
              && itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta) {
            if (blockStateMeta.getBlockState() instanceof final ShulkerBox shulker) {
              for (ItemStack content : shulker.getInventory().getContents()) {
                if (content == null) {
                  continue;
                }

                if (customModelData) {
                  if (!content.hasItemMeta() || !content.getItemMeta().hasCustomModelData()) {
                    continue;
                  }

                  if (itemInMainHand.getItemMeta().getCustomModelData() == content.getItemMeta()
                      .getCustomModelData()) {
                    i += content.getAmount();
                  }
                } else if (content.getType() == material) {
                  i += content.getAmount();
                }
              }
            }
          }
        }

        if (customModelData) {
          if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasCustomModelData()) {
            continue;
          }

          if (itemInMainHand.getItemMeta().getCustomModelData() == itemStack.getItemMeta()
              .getCustomModelData()) {
            i += itemStack.getAmount();
          }
        } else if (itemStack.getType() == material) {
          i += itemStack.getAmount();
        }
      }

      for (ItemStack itemStack : inventory) {
        if (itemStack == null) {
          continue;
        }

        if (itemStack.getType().toString().contains("SHULKER_BOX")) {
          if (itemStack.hasItemMeta()
              && itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta) {
            if (blockStateMeta.getBlockState() instanceof final ShulkerBox shulker) {
              for (ItemStack content : shulker.getInventory().getContents()) {
                if (content == null) {
                  continue;
                }

                if (customModelData) {
                  if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasCustomModelData()) {
                    continue;
                  }

                  if (itemInMainHand.getItemMeta().getCustomModelData() == itemStack.getItemMeta()
                      .getCustomModelData()) {
                    i += itemStack.getAmount();
                  }
                } else if (itemStack.getType() == material) {
                  i += itemStack.getAmount();
                }
              }
            }
          }

          if (customModelData) {
            if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasCustomModelData()) {
              continue;
            }

            if (itemInMainHand.getItemMeta().getCustomModelData() == itemStack.getItemMeta()
                .getCustomModelData()) {
              i += itemStack.getAmount();
            }
          } else if (itemStack.getType() == material) {
            i += itemStack.getAmount();
          }
        }
      }

      if (i > minAmount) {
        player.sendMessage(playerSyncData.getPlayerName() + " >> " + i + " szt.");
      }

    }

  }
}