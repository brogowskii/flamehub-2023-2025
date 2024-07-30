package io.github.flamehub.commons.bukkit.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public final class TextUtil {

  public static final TextComponent RESET = Component.text()
      .decoration(TextDecoration.ITALIC, false)
      .build();
  private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
  private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
      .character('&')
      .hexColors()
      .build();

  private TextUtil() {

  }

  public static Component parse(String text) {
    if (text == null || text.isEmpty()) {
      return Component.empty();
    }

    return RESET.append(LEGACY_COMPONENT_SERIALIZER.deserialize(text));
  }

  public static String serialize(Component component) {
    return LEGACY_COMPONENT_SERIALIZER.serialize(component);
  }

  public static List<Component> parse(List<String> text) {
    List<Component> list = new ArrayList<>();
    text.forEach(it -> list.add(parse(it)));
    return list;
  }

  public static String legacyColor(String text) {
    char colorChar = ChatColor.COLOR_CHAR;

    Matcher matcher = HEX_PATTERN.matcher(text);
    StringBuffer buffer = new StringBuffer(text.length() + 4 * 8);

    while (matcher.find()) {
      final String group = matcher.group(1);

      matcher.appendReplacement(buffer, colorChar + "x"
          + colorChar + group.charAt(0) + colorChar + group.charAt(1)
          + colorChar + group.charAt(2) + colorChar + group.charAt(3)
          + colorChar + group.charAt(4) + colorChar + group.charAt(5));
    }

    return color(matcher.appendTail(buffer).toString());
  }


  public static List<String> legacyColor(List<String> text) {
    List<String> colored = new ArrayList<>();
    text.forEach(it -> colored.add(legacyColor(it)));

    return colored;
  }

  public static String color(String text) {
    return ChatColor.translateAlternateColorCodes('&', text
        .replace(">>", "»")
        .replace("<<", "«"));
  }

  public static List<String> color(List<String> text) {
    List<String> colored = new ArrayList<>();
    text.forEach(it -> colored.add(color(it)));

    return colored;
  }

  public static String progress(int current, int max, int bars, String symbol,
      String completedColor, String notCompletedColor) {
    float percent = current / (float) max;
    int progressBars = (int) (bars * percent);
    int leftOver = bars - progressBars;
    StringBuilder builder = new StringBuilder();
    if (current > max) {
      builder.append(completedColor);
      builder.append(String.valueOf(symbol).repeat(Math.max(0, bars)));
      return builder.toString();
    }

    builder.append(completedColor);
    builder.append(String.valueOf(symbol).repeat(Math.max(0, progressBars)));

    builder.append(notCompletedColor);
    builder.append(String.valueOf(symbol).repeat(Math.max(0, leftOver)));

    return legacyColor(builder.toString());
  }

  public static String tpsWithFormat(double tps) {
    return (tps > 20D ? "*" : "") + Math.min(Math.round(tps * 100D) / 100D, 20D);
  }
}
