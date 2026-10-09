package fr.ada.java_blog;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaBlogApplication {

  public static void main(String[] args) {
    // .env à la racine du monorepo (make backend depuis backend-api/)
    Dotenv dotenv = Dotenv.configure().directory("..").filename(".env").ignoreIfMissing().load();

    dotenv
        .entries()
        .forEach(
            entry -> {
              if (System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
              }
            });

    SpringApplication.run(JavaBlogApplication.class, args);
  }
}
