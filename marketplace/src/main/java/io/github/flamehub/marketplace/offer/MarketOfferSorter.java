package io.github.flamehub.marketplace.offer;

import io.github.flamehub.marketplace.category.MarketCategory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.naming.Name;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class MarketOfferSorter {

  public List<MarketOffer> sorted(
      final MarketCategory category,
      final MarketOfferSort sortType,
      final Collection<MarketOffer> marketOffers,
      final MarketOfferFilter filter,
      final double economyBalance) {

    List<MarketOffer> marketOffersSorted = new ArrayList<>(marketOffers);

    if (category != null) {
      final List<String> materials = category.getMaterials();
      final String enchantmentStr = category.getEnchantment();

      marketOffersSorted = marketOffers.stream()
          .filter(offer -> {
            final ItemStack itemStack = offer.getItem().getItemStack();

            if (enchantmentStr != null && !enchantmentStr.isEmpty()) {
              String[] enchants = enchantmentStr.split(",");
              for (String enchPart : enchants) {
                String[] parts = enchPart.split(":");
                if (parts.length != 2) continue;
                Enchantment ench = Enchantment.getByName(parts[0]);
                if (ench == null) {
                  return true;
                }
                int requiredLevel;
                try {
                  requiredLevel = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                  continue;
                }
                if (itemStack.getEnchantmentLevel(ench) == requiredLevel) {
                  return true;
                }
              }
              return false;
            }

            if (category.getCustomModelData() != 0) {
              return itemStack.hasItemMeta()
                  && itemStack.getItemMeta().hasCustomModelData()
                  && itemStack.getItemMeta().getCustomModelData() == category.getCustomModelData();
            }

            return materials.contains(itemStack.getType().toString());
          })
          .collect(Collectors.toList());
    }

    if (filter == MarketOfferFilter.ENOUGH_MONEY) {
      marketOffersSorted = marketOffersSorted.stream()
          .filter(offer -> offer.getPrice().doubleValue() <= economyBalance)
          .collect(Collectors.toList());
    }

    switch (sortType) {

      case ASCENDING_PRICE -> marketOffersSorted.sort(
          Comparator.comparingDouble(o -> o.getPrice().doubleValue()));

      case DESCENDING_PRICE -> marketOffersSorted.sort(
          (o1, o2) -> Double.compare(o2.getPrice().doubleValue(), o1.getPrice().doubleValue()));

      case NEWEST -> marketOffersSorted.sort((o1, o2) ->
          Long.compare(
              o2.getCreationDate().toInstant().toEpochMilli(),
              o1.getCreationDate().toInstant().toEpochMilli())
      );

      case LEVEL -> marketOffersSorted.sort(
          Comparator.comparingInt((MarketOffer o) -> o.getItem().getItemStack()
                  .getEnchantmentLevel(Enchantment.DURABILITY))
              .reversed()
      );

      case OLDEST -> marketOffersSorted.sort(
          Comparator.comparingLong(o -> o.getCreationDate().toInstant().toEpochMilli())
      );

      case NONE -> {
      }

    }

    return marketOffersSorted;

  }

}
