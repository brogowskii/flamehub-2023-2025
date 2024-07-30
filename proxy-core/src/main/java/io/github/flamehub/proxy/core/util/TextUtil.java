package io.github.flamehub.proxy.core.util;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TextUtil {

  private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
      .character('&')
      .hexColors()
      .build();

  public static Component parse(String text) {
    if (text == null || text.isEmpty()) {
      return Component.empty();
    }

    return LEGACY_COMPONENT_SERIALIZER.deserialize(text);
  }

  public static String serialize(Component component) {
    return LEGACY_COMPONENT_SERIALIZER.serialize(component);
  }

  public static PreLoginEvent.PreLoginComponentResult preDenied(String text) {
    return PreLoginEvent.PreLoginComponentResult.denied(TextUtil.parse(text));
  }

  public static ResultedEvent.ComponentResult resultedDenied(String text) {
    return ResultedEvent.ComponentResult.denied(TextUtil.parse(text));
  }


}
