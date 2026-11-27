package fr.alltech.eca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application ECA Backend.
 * Initialise et démarre l'application Spring Boot.
 */
@SpringBootApplication
public class EcaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcaBackendApplication.class, args);
    }

}
