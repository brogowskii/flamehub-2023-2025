package io.github.flamehub.commons.bukkit.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public final class TextUtil {

  public static final TextComponent RESET = Component.text()
      .decoration(TextDecoration.ITALIC, false)
      .build();
  public static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
      .character('&')
      .hexColors()
      .build();
  private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
  private static final TextReplacementConfig LEGACY_REPLACEMENT_CONFIG = TextReplacementConfig.builder()
      .match(Pattern.compile(".*"))
      .replacement(
          (matchResult, build) -> LEGACY_COMPONENT_SERIALIZER.deserialize(matchResult.group()))
      .build();

  public static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
      .postProcessor(component -> component.replaceText(LEGACY_REPLACEMENT_CONFIG))
      .build();

  private TextUtil() {

  }

  public static Component parse(final String text) {
    if (text == null || text.isEmpty()) {
      return Component.empty();
    }

    return RESET.append(LEGACY_COMPONENT_SERIALIZER.deserialize(text));
  }

  public static String serialize(final Component component) {
    if (component == null) {
      return "";
    }
    return LEGACY_COMPONENT_SERIALIZER.serialize(component);
  }

  public static List<Component> parse(final List<String> text) {
    final List<Component> list = new ArrayList<>();
    text.forEach(it -> list.add(parse(it)));
    return list;
  }

  public static String legacyColor(final String text) {
    final char colorChar = ChatColor.COLOR_CHAR;

    final Matcher matcher = HEX_PATTERN.matcher(text);
    final StringBuffer buffer = new StringBuffer(text.length() + 4 * 8);

    while (matcher.find()) {
      final String group = matcher.group(1);

      matcher.appendReplacement(buffer, colorChar + "x"
          + colorChar + group.charAt(0) + colorChar + group.charAt(1)
          + colorChar + group.charAt(2) + colorChar + group.charAt(3)
          + colorChar + group.charAt(4) + colorChar + group.charAt(5));
    }

    return color(matcher.appendTail(buffer).toString());
  }


  public static List<String> legacyColor(final List<String> text) {
    final List<String> colored = new ArrayList<>();
    text.forEach(it -> colored.add(legacyColor(it)));

    return colored;
  }

  public static String color(final String text) {
    return ChatColor.translateAlternateColorCodes('&', text
        .replace(">>", "»")
        .replace("<<", "«"));
  }

  public static List<String> color(final List<String> text) {
    final List<String> colored = new ArrayList<>();
    text.forEach(it -> colored.add(color(it)));

    return colored;
  }

  public static String progress(final int current, final int max, final int bars, final String symbol,
      final String completedColor, final String notCompletedColor) {
    final float percent = current / (float) max;
    final int progressBars = (int) (bars * percent);
    final int leftOver = bars - progressBars;
    final StringBuilder builder = new StringBuilder();
    if (current > max) {
      builder.append(completedColor);
      builder.append(String.valueOf(symbol).repeat(Math.max(0, bars)));
      return builder.toString();
    }

    builder.append(completedColor);
    builder.append(String.valueOf(symbol).repeat(Math.max(0, progressBars)));

    builder.append(notCompletedColor);
    builder.append(String.valueOf(symbol).repeat(Math.max(0, leftOver)));

    return builder.toString();
  }

  public static String tpsWithFormat(final double tps) {
    return (tps > 20D ? "*" : "") + Math.min(Math.round(tps * 100D) / 100D, 20D);
  }
}
