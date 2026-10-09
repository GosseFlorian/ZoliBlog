package fr.ada.java_blog.config;

import fr.ada.java_blog.util.LogSanitizer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Journalise chaque requête HTTP sans données sensibles (OWASP A09). */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestAuditFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(RequestAuditFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    long start = System.currentTimeMillis();

    try {
      filterChain.doFilter(request, response);
    } finally {
      long durationMs = System.currentTimeMillis() - start;
      String method = request.getMethod();
      String path = LogSanitizer.sanitizePath(request.getRequestURI());
      int status = response.getStatus();
      String ip = LogSanitizer.maskIp(request.getRemoteAddr());

      if (status >= 400) {
        log.warn("HTTP {} {} : {} ({} ms, ip={})", method, path, status, durationMs, ip);
      } else if (log.isDebugEnabled()) {
        log.debug("HTTP {} {} : {} ({} ms, ip={})", method, path, status, durationMs, ip);
      }
    }
  }
}
