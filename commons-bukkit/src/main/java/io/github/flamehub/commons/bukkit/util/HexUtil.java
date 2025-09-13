package io.github.flamehub.commons.bukkit.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HexUtil {

  private HexUtil() {
  }

  public static String interpolateColors(final String message, final String hex1, final String hex2, final boolean isBold) {
    final int[] rgb1 = hexToRGB(hex1);
    final int[] rgb2 = hexToRGB(hex2);

    final StringBuilder result = new StringBuilder();
    final int stepCount = message.length();

    for (int i = 0; i < stepCount; i++) {
      final float fraction = (float) i / (stepCount - 1);
      final int r = (int) (rgb1[0] + fraction * (rgb2[0] - rgb1[0]));
      final int g = (int) (rgb1[1] + fraction * (rgb2[1] - rgb1[1]));
      final int b = (int) (rgb1[2] + fraction * (rgb2[2] - rgb1[2]));

      result.append("&").append(rgbToHex(r, g, b));

      if (isBold) {
        result.append("&l");
      }

      result.append(message.charAt(i));
    }

    return result.toString();
  }

  private static int[] hexToRGB(final String hex) {
    return new int[]{
        Integer.valueOf(hex.substring(1, 3), 16),
        Integer.valueOf(hex.substring(3, 5), 16),
        Integer.valueOf(hex.substring(5, 7), 16)
    };
  }

  private static String rgbToHex(final int r, final int g, final int b) {
    return String.format("#%02X%02X%02X", r, g, b);
  }

  public static String extractFirstHex(final String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }

    final Pattern pattern = Pattern.compile("(&#[0-9A-Fa-f]+)");
    final Matcher matcher = pattern.matcher(input);

    if (matcher.find()) {
      return matcher.group(1);
    }
    return "";
  }


}
