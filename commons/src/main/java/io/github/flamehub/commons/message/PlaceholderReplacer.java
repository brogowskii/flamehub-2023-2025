package io.github.flamehub.commons.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderReplacer {

  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}",
      Pattern.CASE_INSENSITIVE);

  public static List<String> replacePlaceholders(List<String> messages,
      Map<String, Object> replacements) {
    List<String> formattedMessages = new ArrayList<>();

    for (String message : messages) {
      formattedMessages.add(replacePlaceholdersInMessage(message, replacements));
    }

    return formattedMessages;
  }

  private static String replacePlaceholdersInMessage(String message,
      Map<String, Object> replacements) {
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
    StringBuffer result = new StringBuffer();

    while (matcher.find()) {
      String placeholder = matcher.group(1);
      Object replacement = replacements.get(placeholder.toLowerCase());
      String replacementString = replacement != null ? replacement.toString() : "";
      String sanitizedReplacement = Matcher.quoteReplacement(replacementString);

      matcher.appendReplacement(result, sanitizedReplacement);
    }

    matcher.appendTail(result);
    return result.toString();
  }

}





