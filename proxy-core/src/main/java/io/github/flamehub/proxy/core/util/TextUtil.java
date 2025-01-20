package io.github.flamehub.proxy.core.util;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TextUtil {


  public static final TextComponent RESET = Component.text()
      .decoration(TextDecoration.ITALIC, false)
      .build();

  private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
      .character('&')
      .hexColors()
      .build();

  private static final TextReplacementConfig LEGACY_REPLACEMENT_CONFIG = TextReplacementConfig.builder()
      .match(Pattern.compile(".*"))
      .replacement((matchResult, build) -> parse(matchResult.group()))
      .build();

  public static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
      .postProcessor(component -> component.replaceText(LEGACY_REPLACEMENT_CONFIG))
      .build();

  private TextUtil() {

  }

  public static Component parse(String text) {
    if (text == null || text.isEmpty()) {
      return Component.empty();
    }

    return RESET.append(LEGACY_COMPONENT_SERIALIZER.deserialize(text));
  }

  public static List<Component> parse(List<String> text) {
    List<Component> list = new ArrayList<>();
    text.forEach(it -> list.add(parse(it)));
    return list;
  }

  public static String serialize(Component component) {
    return LEGACY_COMPONENT_SERIALIZER.serialize(component);
  }

  public static PreLoginEvent.PreLoginComponentResult preDenied(Component text) {
    return PreLoginEvent.PreLoginComponentResult.denied(text);
  }

  public static PreLoginEvent.PreLoginComponentResult preDenied(String text) {
    return PreLoginEvent.PreLoginComponentResult.denied(TextUtil.parse(text));
  }

  public static ResultedEvent.ComponentResult resultedDenied(String text) {
    return ResultedEvent.ComponentResult.denied(TextUtil.parse(text));
  }

  public static ResultedEvent.ComponentResult resultedDenied(Component text) {
    return ResultedEvent.ComponentResult.denied(text);
  }


}
