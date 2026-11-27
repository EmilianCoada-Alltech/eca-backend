
package fr.alltech.eca.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure l'initialisation de la base de données MongoDB avec Liquibase.
 */
@Configuration
public class LiquibaseConfiguration {

    /**
     * Chemin vers le changelog principal contenant les scripts d'initialisation.
     */
    private static final String CHANGELOG =
            "db/changelog/db.changelog-master.xml";

    private static final Logger log =
            LoggerFactory.getLogger(LiquibaseConfiguration.class);

    /**
     * Exécute les migrations Liquibase au démarrage de l'application
     * lorsque la propriété eca.liquibase.enabled est activée.
     *
     * @param mongoUri URI de connexion à la base de données MongoDB
     * @return le composant chargé d'exécuter l'initialisation
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "eca.liquibase",
            name = "enabled",
            havingValue = "true"
    )
    public ApplicationRunner liquibaseRunner(
            @Value("${spring.mongodb.uri}") String mongoUri) {

        return args -> {

            log.info("Starting Liquibase database initialization...");

            try (Database database = DatabaseFactory.getInstance()
                    .openDatabase(
                            mongoUri,
                            null,
                            null,
                            null,
                            null
                    )) {

                try (Liquibase liquibase = new Liquibase(
                        CHANGELOG,
                        new ClassLoaderResourceAccessor(),
                        database
                )) {

                    liquibase.update("");
                }
            }

            log.info("Liquibase database initialization completed.");
        };
    }
}
