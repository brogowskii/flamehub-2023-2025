package io.github.flamehub.commons.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderReplacer {

  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}",
      Pattern.CASE_INSENSITIVE);

  public static List<String> replacePlaceholders(final List<String> messages,
      final Map<String, Object> replacements) {
    final List<String> formattedMessages = new ArrayList<>();

    for (final String message : messages) {
      formattedMessages.add(replacePlaceholdersInMessage(message, replacements));
    }

    return formattedMessages;
  }

  private static String replacePlaceholdersInMessage(final String message,
      final Map<String, Object> replacements) {
    final Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
    final StringBuffer result = new StringBuffer();

    while (matcher.find()) {
      final String placeholder = matcher.group(1);
      final Object replacement = replacements.get(placeholder.toLowerCase());
      final String replacementString = replacement != null ? replacement.toString() : "";
      final String sanitizedReplacement = Matcher.quoteReplacement(replacementString);

      matcher.appendReplacement(result, sanitizedReplacement);
    }

    matcher.appendTail(result);
    return result.toString();
  }

}





