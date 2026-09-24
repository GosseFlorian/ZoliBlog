package fr.ada.java_blog.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LogSanitizerTest {

  @Test
  void sanitizePath_null_retourneSlash() {
    assertEquals("/", LogSanitizer.sanitizePath(null));
  }

  @Test
  void sanitizePath_retireLesRetoursLigne() {
    assertEquals("/articles/1", LogSanitizer.sanitizePath("/articles/1\r\n"));
  }

  @Test
  void maskEmail_null_retourneMasque() {
    assertEquals("***", LogSanitizer.maskEmail(null));
  }

  @Test
  void maskEmail_sansArobase_retourneMasque() {
    assertEquals("***", LogSanitizer.maskEmail("invalid"));
  }

  @Test
  void maskEmail_courant_masqueLeLocal() {
    assertEquals("a***@example.com", LogSanitizer.maskEmail("alice@example.com"));
  }

  @Test
  void maskEmail_courtLocal_masqueEntierement() {
    assertEquals("***@example.com", LogSanitizer.maskEmail("a@example.com"));
  }

  @Test
  void maskIp_null_retourneUnknown() {
    assertEquals("unknown", LogSanitizer.maskIp(null));
  }

  @Test
  void maskIp_vide_retourneUnknown() {
    assertEquals("unknown", LogSanitizer.maskIp("   "));
  }

  @Test
  void maskIp_ipv4_masqueLeDernierOctet() {
    assertEquals("192.168.1.xxx", LogSanitizer.maskIp("192.168.1.42"));
  }

  @Test
  void maskIp_sansPoint_retourneMasque() {
    assertEquals("xxx", LogSanitizer.maskIp("localhost"));
  }
}
