package io.github.flamehub.player.sync.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.util.SerializationUtil;
import io.github.flamehub.player.sync.data.PlayerSyncData;
import io.github.flamehub.player.sync.data.PlayerSyncDataRepository;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.List;

@Command(name = "scanusers")
@Permission("scan")
public class ScanUsersCommand {

    private final FlameDispatcher flameDispatcher;
    private final PlayerSyncDataRepository playerSyncDataRepository;

    public ScanUsersCommand(FlameDispatcher flameDispatcher, PlayerSyncDataRepository playerSyncDataRepository) {
        this.flameDispatcher = flameDispatcher;
        this.playerSyncDataRepository = playerSyncDataRepository;
    }

    @Execute
    void exec(@Context Player player, @Arg Material material, @Arg int minAmount) {


        this.flameDispatcher.dispatchAsync(() -> {
            player.sendMessage("ładuje wszystkie dane graczy....");
            List<PlayerSyncData> playerSyncDataList = this.playerSyncDataRepository.loadAll();

            player.sendMessage("Dane załadowane, rozpoczynam iterację.");
            player.sendMessage("size: " + playerSyncDataList.size());
            for (PlayerSyncData playerSyncData : playerSyncDataList) {

                ItemStack[] inventory;
                ItemStack[] enderChest;
                try {
                    enderChest = (ItemStack[]) SerializationUtil.deserializeBukkitObject(playerSyncData.getSerializedEnderchest());
                    inventory = (ItemStack[]) SerializationUtil.deserializeBukkitObject(playerSyncData.getSerializedInventory());
                }
                catch (Exception e) {
                    continue;
                }

                if (enderChest == null || inventory == null) {
                    continue;
                }

                int i = 0;
                for (ItemStack itemStack : enderChest) {


                    if (itemStack == null) {
                        continue;
                    }

                    if (itemStack.getType().toString().contains("SHULKER_BOX")) {
                        if (itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta) {

                            if (blockStateMeta.getBlockState() instanceof ShulkerBox) {
                                ShulkerBox shulker = (ShulkerBox) blockStateMeta.getBlockState();

                                for (ItemStack content : shulker.getInventory().getContents()) {
                                    if (content == null) {
                                        continue;
                                    }

                                    if (content.getType() == material) {
                                        i += content.getAmount();
                                    }
                                }
                            }
                        }
                    }

                    if (itemStack.getType() == material) {

//                    if (optName.isPresent()) {
//                        if (!TextUtil.serialize(itemStack.getItemMeta().displayName()).contains(optName.get())) {
//                            continue;
//                        }
//                    }

                        i += itemStack.getAmount();
                    }

                }

                for (ItemStack itemStack : inventory) {

                    if (itemStack == null) {
                        continue;
                    }

//                if (optName.isPresent()) {
//                    if (!TextUtil.serialize(itemStack.getItemMeta().displayName()).contains(optName.get())) {
//                        continue;
//                    }
//                }

                    if (itemStack.getType().toString().contains("SHULKER_BOX")) {
                        if (itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta) {

                            if (blockStateMeta.getBlockState() instanceof ShulkerBox) {
                                ShulkerBox shulker = (ShulkerBox) blockStateMeta.getBlockState();

                                for (ItemStack content : shulker.getInventory().getContents()) {
                                    if (content == null) {
                                        continue;
                                    }

                                    if (content.getType() == material) {
                                        i += content.getAmount();
                                    }
                                }
                            }
                        }
                    }

                    if (itemStack.getType() == material) {
                        i += itemStack.getAmount();
                    }
                }


                if (i > minAmount) {
                    player.sendMessage(playerSyncData.getPlayerName() + " >> " + i + " szt.");
                }

            }

            player.sendMessage("Zakończono...");
        });



    }

}
