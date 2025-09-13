package io.github.flamehub.armor.upgrade;

import org.bukkit.Color;

public enum ArmorColor {

  WHITE,
  ORANGE,
  YELLOW,
  SILVER,
  LIME,
  GRAY,
  PURPLE,
  BLUE,
  BROWN,
  GREEN,
  RED,
  MAROON,
  OLIVE,
  FUCHSIA,
  NAVY,
  TEAL,
  BLACK;

  public static Color get(final ArmorColor armorColor) {
    return switch (armorColor) {
      case WHITE -> Color.WHITE;
      case ORANGE -> Color.ORANGE;
      case YELLOW -> Color.YELLOW;
      case SILVER -> Color.SILVER;
      case LIME -> Color.LIME;
      case GRAY -> Color.GRAY;
      case PURPLE -> Color.PURPLE;
      case BLUE -> Color.BLUE;
      case GREEN -> Color.GREEN;
      case RED -> Color.RED;
      case MAROON -> Color.MAROON;
      case OLIVE -> Color.OLIVE;
      case FUCHSIA -> Color.FUCHSIA;
      case NAVY -> Color.NAVY;
      case TEAL -> Color.TEAL;
      case BLACK -> Color.BLACK;
      default -> null;
    };

  }

}
