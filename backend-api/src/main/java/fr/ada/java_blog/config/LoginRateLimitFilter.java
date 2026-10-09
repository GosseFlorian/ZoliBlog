package fr.ada.java_blog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

  private static final int MAX_ATTEMPTS = 10;
  private static final long WINDOW_MS = 5 * 60 * 1000L;

  private final boolean enabled;
  private final Map<String, AttemptWindow> attemptsByIp = new ConcurrentHashMap<>();

  public LoginRateLimitFilter(@Value("${security.login-rate-limit.enabled:true}") boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!enabled
        || !"POST".equalsIgnoreCase(request.getMethod())
        || !"/auth/login".equals(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    String ip = request.getRemoteAddr();
    AttemptWindow window = attemptsByIp.computeIfAbsent(ip, key -> new AttemptWindow());

    synchronized (window) {
      window.resetIfExpired();
      if (window.count.get() >= MAX_ATTEMPTS) {
        response.sendError(
            HttpStatus.TOO_MANY_REQUESTS.value(),
            "Trop de tentatives de connexion. Réessayez dans quelques minutes.");
        return;
      }
      window.count.incrementAndGet();
    }

    filterChain.doFilter(request, response);
  }

  private static final class AttemptWindow {
    private final AtomicInteger count = new AtomicInteger(0);
    private long windowStart = Instant.now().toEpochMilli();

    void resetIfExpired() {
      long now = Instant.now().toEpochMilli();
      if (now - windowStart > WINDOW_MS) {
        count.set(0);
        windowStart = now;
      }
    }
  }
}
