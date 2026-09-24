package fr.ada.java_blog.config;

import fr.ada.java_blog.util.LogSanitizer;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final RequestAuditFilter requestAuditFilter;
  private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
  private final LoginRateLimitFilter loginRateLimitFilter;

  public SecurityConfig(
      JwtAuthFilter jwtAuthFilter,
      RequestAuditFilter requestAuditFilter,
      LoginRateLimitFilter loginRateLimitFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.requestAuditFilter = requestAuditFilter;
    this.loginRateLimitFilter = loginRateLimitFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .headers(
            headers ->
                headers
                    .contentSecurityPolicy(
                        csp ->
                            csp.policyDirectives(
                                "default-src 'self'; frame-ancestors 'none'; form-action 'self'; base-uri 'self'"))
                    .frameOptions(frame -> frame.deny())
                    .contentTypeOptions(Customizer.withDefaults())
                    .referrerPolicy(
                        referrer ->
                            referrer.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy
                                    .STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))
        .authorizeHttpRequests(
            auth ->
                auth
                    // Preflight CORS (navigateur) — doit rester public
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    // Login / inscription
                    .requestMatchers("/auth/login", "/auth/register")
                    .permitAll()
                    // Poster un commentaire nécessite d'être connecté (le userId
                    // envoyé est vérifié dans CommentaireController à partir du
                    // token — voir ci-dessous). Doit être déclaré AVANT la règle
                    // générale /articles/** pour prendre le dessus.
                    .requestMatchers(HttpMethod.POST, "/articles/*/commentaires")
                    .authenticated()
                    // Modifier un commentaire nécessite d'être connecté ; le contrôle
                    // "c'est bien SON commentaire" est fait dans CommentaireController
                    // (pas exprimable ici, il faut lire le commentaire en base).
                    .requestMatchers(HttpMethod.PATCH, "/commentaires/*")
                    .authenticated()
                    // supprimer un commentaire nécessite d'être connecté (site/) ; le contrôle
                    // "c'est bien SON commentaire" est fait dans CommentaireController
                    // (pas exprimable ici, il faut lire le commentaire en base).
                    .requestMatchers(HttpMethod.DELETE, "/commentaires/*")
                    .authenticated()
                    // API publique (lecture articles, santé)
                    .requestMatchers("/articles/**", "/ping", "/db/**")
                    .permitAll()
                    // Back-office — rôle ADMIN obligatoire
                    .requestMatchers("/admin/**")
                    .hasAuthority("ADMIN")
                    .anyRequest()
                    .permitAll())
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (request, response, authException) -> {
                          log.warn(
                              "Acces refuse - non authentifie ({} {})",
                              request.getMethod(),
                              LogSanitizer.sanitizePath(request.getRequestURI()));
                          response.sendError(
                              HttpServletResponse.SC_UNAUTHORIZED, "Non authentifie");
                        })
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          log.warn(
                              "Acces refuse - role insuffisant ({} {})",
                              request.getMethod(),
                              LogSanitizer.sanitizePath(request.getRequestURI()));
                          response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acces refuse");
                        }))
        // Bloquer l'abus le plus tôt possible
        .addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
        // Logger la requête (y compris les 429)
        .addFilterBefore(requestAuditFilter, UsernamePasswordAuthenticationFilter.class)
        // Vérifier le token JWT sur les routes protégées
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
