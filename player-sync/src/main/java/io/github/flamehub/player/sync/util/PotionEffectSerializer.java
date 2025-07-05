package io.github.flamehub.player.sync.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PotionEffectSerializer {

  private PotionEffectSerializer() {
  }

  public static String serializePotionEffects(final Collection<PotionEffect> effects) {
    final StringBuilder serialized = new StringBuilder();
    for (final PotionEffect effect : effects) {
      serialized.append(effect.getType().getName()).append(";")
          .append(effect.getDuration()).append(";")
          .append(effect.getAmplifier()).append("|");
    }
    return serialized.toString();
  }

  public static List<PotionEffect> deserializePotionEffects(final String serialized) {
    final List<PotionEffect> effects = new ArrayList<>();
    if (serialized.isEmpty()) {
      return effects;
    }

    final String[] splitEffects = serialized.split("\\|");
    for (final String splitEffect : splitEffects) {
      final String[] parts = splitEffect.split(";");
      final PotionEffectType type = PotionEffectType.getByName(parts[0]);
      final int duration = Integer.parseInt(parts[1]);
      final int amplifier = Integer.parseInt(parts[2]);
      effects.add(new PotionEffect(type, duration, amplifier));
    }
    return effects;
  }

}
