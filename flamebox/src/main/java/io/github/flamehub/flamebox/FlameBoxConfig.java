package io.github.flamehub.flamebox;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@FlameConfigProperties(name = "flameBox.json")
@EnableRemote(collection = "configs")
public final class FlameBoxConfig extends FlameConfig {

  private boolean enabled = true;

  private Map<Integer, FlameBoxDrop> drops = new HashMap<>();
  private Set<Location> previewLocation = new HashSet<>();
  private ItemStack flameBoxItem = FlameItemBuilder.of(Material.NOTE_BLOCK)
      .glow()
      .name(
          "&#C40505&lғ&#D40707&lʟ&#E40909&lᴀ&#F40B0B&lᴍ&#F40B0B&lᴇ&#E40909&lʙ&#D40707&lᴏ&#C40505&lx")
      .lore(
          "",
          " &#FF3B3Bᴊ&#FF4141ᴇ&#FE4747ᴅ&#FE4D4Dᴇ&#FE5353ɴ &#FD5A5Aᴢ &#FD6060ɴ&#FD6666ᴀ&#FC6C6Cᴊ&#FC6C6Cʀ&#FD6666ᴢ&#FD6060ᴀ&#FD5A5Aᴅ&#FE5353s&#FE4D4Dᴢ&#FE4747ʏ&#FF4141ᴄ&#FF3B3Bʜ",
          " &#FF3B3Bᴘ&#FF4040ʀ&#FE4545ᴢ&#FE4B4Bᴇ&#FE5050ᴅ&#FE5555ᴍ&#FD5A5Aɪ&#FD5F5Fᴏ&#FD6565ᴛ&#FC6A6Aᴏ&#FC6F6Fᴡ &#FC6A6Aɴ&#FD6565ᴀ &#FD5F5Fs&#FD5A5Aᴇ&#FE5555ʀ&#FE5050ᴡ&#FE4B4Bᴇ&#FE4545ʀ&#FF4040ᴢ&#FF3B3Bᴇ",
          "",
          "&eᴘᴏsᴛᴀᴡ ɴᴀ ᴢɪᴇᴍɪ ᴀʙʏ ᴡʏʟᴏsᴏᴡᴀᴄ."
      )
      .asItemStack();

  public FlameBoxConfig() {
  }

  @JsonIgnore
  public FlameBoxDrop random() {
    if (drops.isEmpty()) {
      return null;
    }

    final List<FlameBoxDrop> values = new ArrayList<>(values());
    final double totalChances = values.stream()
        .mapToDouble(FlameBoxDrop::getChance)
        .sum();
    double randomValue = Math.random() * totalChances;

    for (final FlameBoxDrop scratchDrop : values) {
      randomValue -= scratchDrop.getChance();
      if (randomValue <= 0) {
        return scratchDrop;
      }
    }

    return values.get(values.size() - 1);
  }

  public ItemStack getFlameBoxItem() {
    return flameBoxItem;
  }

  public void setFlameBoxItem(final ItemStack flameBoxItem) {
    this.flameBoxItem = flameBoxItem;
  }

  public Set<Location> getPreviewLocation() {
    return previewLocation;
  }

  public Map<Integer, FlameBoxDrop> getDrops() {
    return drops;
  }

  public Collection<FlameBoxDrop> values() {
    return drops.values();
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
