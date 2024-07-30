package io.github.flamehub.essentials.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command(name = "kosz")
public class BinCommand {

  @Execute
  void exec(@Context Player player) {
    player.openInventory(Bukkit.createInventory(null, 54, TextUtil.parse("&8&lKosz")));
  }

}
