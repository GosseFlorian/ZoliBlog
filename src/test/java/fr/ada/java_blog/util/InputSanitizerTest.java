package fr.ada.java_blog.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputSanitizerTest {

    @Test
    void stripDangerousHtml_null_retourneNull() {
        assertNull(InputSanitizer.stripDangerousHtml(null));
    }

    @Test
    void stripDangerousHtml_retireScriptTag() {
        String input = "Hello<script>alert('x')</script>World";
        assertEquals("HelloWorld", InputSanitizer.stripDangerousHtml(input));
    }

    @Test
    void stripDangerousHtml_retireEventHandler() {
        String input = "<div onclick=evil()>Click</div>";
        assertEquals("<div evil()>Click</div>", InputSanitizer.stripDangerousHtml(input));
    }

    @Test
    void looksLikeSqlInjection_null_retourneFalse() {
        assertFalse(InputSanitizer.looksLikeSqlInjection(null));
    }

    @Test
    void looksLikeSqlInjection_vide_retourneFalse() {
        assertFalse(InputSanitizer.looksLikeSqlInjection("   "));
    }

    @Test
    void looksLikeSqlInjection_texteNormal_retourneFalse() {
        assertFalse(InputSanitizer.looksLikeSqlInjection("Un article sur Java"));
    }

    @Test
    void looksLikeSqlInjection_commentaireSql_retourneTrue() {
        assertTrue(InputSanitizer.looksLikeSqlInjection("admin'--"));
    }

    @Test
    void looksLikeSqlInjection_dropTable_retourneTrue() {
        assertTrue(InputSanitizer.looksLikeSqlInjection("'; DROP TABLE users;"));
    }

    @Test
    void looksLikeSqlInjection_unionSelect_retourneTrue() {
        assertTrue(InputSanitizer.looksLikeSqlInjection("1 UNION SELECT password"));
    }
}
