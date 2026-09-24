package fr.ada.java_blog.util;

public final class LogSanitizer {

  private LogSanitizer() {
    // classe utilitaire — pas d'instanciation
  }

  /** Retire les retours ligne du path (évite log injection). */
  public static String sanitizePath(String path) {
    if (path == null) {
      return "/";
    }
    return path.replaceAll("[\\r\\n]", "");
  }

  /** a***@example.com — le domaine reste lisible pour le debug. */
  public static String maskEmail(String mail) {
    if (mail == null || !mail.contains("@")) {
      return "***";
    }
    int at = mail.indexOf('@');
    if (at <= 1) {
      return "***@" + mail.substring(at + 1);
    }
    return mail.charAt(0) + "***@" + mail.substring(at + 1);
  }

  /** 192.168.1.xxx — repère une IP sans la stocker en clair. */
  public static String maskIp(String ip) {
    if (ip == null || ip.isBlank()) {
      return "unknown";
    }
    int lastDot = ip.lastIndexOf('.');
    if (lastDot > 0) {
      return ip.substring(0, lastDot) + ".xxx";
    }
    return "xxx";
  }
}
