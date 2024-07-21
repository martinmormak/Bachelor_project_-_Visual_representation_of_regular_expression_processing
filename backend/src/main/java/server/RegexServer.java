package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class RegexServer {

    public static void main(String[] args) {
        SpringApplication.run(RegexServer.class, args);
    }
}
