package io.github.flamehub.marketplace.offer;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.PostLoad;
import io.github.flamehub.marketplace.MarketSerializer;
import java.io.IOException;
import org.bukkit.inventory.ItemStack;

@Entity
public final class MarketOfferItem {

  private String serializedItemStack;
  private transient ItemStack itemStack;

  public MarketOfferItem() {
  }

  public MarketOfferItem(final String serializedItemStack) {
    this.serializedItemStack = serializedItemStack;
    try {
      postLoad();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @PostLoad
  public void postLoad() throws IOException {
    this.itemStack = MarketSerializer.deserialize(serializedItemStack);
  }

  public String getSerializedItemStack() {
    return serializedItemStack;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }
}
