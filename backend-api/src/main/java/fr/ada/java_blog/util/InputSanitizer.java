package fr.ada.java_blog.util;

import java.util.regex.Pattern;

public final class InputSanitizer {

  // repère <script>...</script> et le supprime
  private static final Pattern SCRIPT_TAG =
      Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  // Repère des attributs du type onclick=, onerror= (autre façon d'exécuter du
  // JS) et les supprime
  private static final Pattern EVENT_HANDLER =
      Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE);

  private InputSanitizer() {}

  public static String stripDangerousHtml(String input) {
    if (input == null) {
      return null;
    }
    String cleaned = SCRIPT_TAG.matcher(input).replaceAll("");
    cleaned = EVENT_HANDLER.matcher(cleaned).replaceAll("");
    return cleaned.trim();
  }

  public static boolean looksLikeSqlInjection(String input) {
    if (input == null || input.isBlank()) {
      return false;
    }
    String lower = input.toLowerCase();
    return lower.contains("--")
        || lower.contains("';")
        || lower.contains("drop table")
        || lower.contains("union select");
  }
}
