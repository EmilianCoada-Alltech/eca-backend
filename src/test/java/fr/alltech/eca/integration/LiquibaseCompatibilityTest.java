
package fr.alltech.eca.integration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class LiquibaseCompatibilityTest {

    private static final String MONGO_URL =
            "mongodb://localhost:27017/liquibase_test";

    private static final String COLLECTION_NAME = "liquibase_probe";

    @Test
    void shouldCreateCollectionUsingLiquibase() throws Exception {

        // Open a connection through the Liquibase MongoDB extension.
        try (Database database = DatabaseFactory.getInstance()
                .openDatabase(
                        MONGO_URL,
                        null,
                        null,
                        null,
                        null
                )) {

            // Load and execute the changelog.
            try (Liquibase liquibase = new Liquibase(
                    "liquibase/test-changelog.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            )) {
                liquibase.update("");
            }
        }

        // Verify independently that the collection was created.
        try (MongoClient mongoClient = MongoClients.create(MONGO_URL)) {

            MongoDatabase mongoDatabase =
                    mongoClient.getDatabase("liquibase_test");

            boolean collectionExists = mongoDatabase
                    .listCollectionNames()
                    .into(new ArrayList<>())
                    .contains(COLLECTION_NAME);

            assertThat(collectionExists)
                    .as("Liquibase should create the MongoDB collection")
                    .isTrue();
        }
    }
}
