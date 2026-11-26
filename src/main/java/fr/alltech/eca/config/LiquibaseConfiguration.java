
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

@Configuration
public class LiquibaseConfiguration {

    private static final String CHANGELOG =
            "db/changelog/db.changelog-master.xml";


    private static final Logger log =
            LoggerFactory.getLogger(LiquibaseConfiguration.class);

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
