package io.github.flamehub.voucher;

import java.util.List;
import org.bukkit.inventory.ItemStack;

public final class Voucher {

  private ItemStack itemStack;
  private List<String> commands;

  public Voucher() {
  }

  public Voucher(final ItemStack itemStack, final List<String> commands) {
    this.itemStack = itemStack;
    this.commands = commands;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public void setItemStack(final ItemStack itemStack) {
    this.itemStack = itemStack;
  }

  public List<String> getCommands() {
    return commands;
  }

  public void setCommands(final List<String> commands) {
    this.commands = commands;
  }
}
