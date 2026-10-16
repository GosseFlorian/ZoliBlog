package fr.ada.java_blog.config;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @NonNull private final String[] allowedOrigins;

  public WebConfig(@Value("${cors.allowed-origins}") String allowedOrigins) {
    this.allowedOrigins = Objects.requireNonNull(allowedOrigins.split(","));
  }

  @Override
  public void addCorsMappings(@NonNull CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins(allowedOrigins)
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("Authorization", "Content-Type", "X-Login-Context")
        .exposedHeaders("Content-Type")
        .maxAge(3600);
  }
}
