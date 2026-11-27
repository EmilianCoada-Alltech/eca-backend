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

/**
 * Test d'intégration de compatibilité entre Liquibase et MongoDB.
 * Vérifie que l'extension MongoDB de Liquibase permet d'exécuter un changelog
 * et de créer correctement une collection.
 */
class LiquibaseCompatibilityTest {

    private static final String MONGO_URL =
            "mongodb://localhost:27017/liquibase_test";

    private static final String COLLECTION_NAME = "liquibase_probe";

    /**
     * Vérifie que Liquibase peut créer une collection MongoDB
     * à partir d'un changelog dédié.
     *
     * @throws Exception en cas d'erreur de connexion ou d'exécution du changelog
     */
    @Test
    void shouldCreateCollectionUsingLiquibase() throws Exception {

        // Connexion à MongoDB via l'extension Liquibase dédiée à MongoDB.
        try (Database database = DatabaseFactory.getInstance()
                .openDatabase(
                        MONGO_URL,
                        null,
                        null,
                        null,
                        null
                )) {

            // Charge et exécute le changelog de test pour appliquer
            // les modifications de structure à la base de données.
            try (Liquibase liquibase = new Liquibase(
                    "liquibase/test-changelog.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            )) {
                liquibase.update("");
            }
        }

        // Vérification indépendante de la création de la collection :
        // utilise le pilote MongoDB directement, sans passer par Liquibase.
        try (MongoClient mongoClient = MongoClients.create(MONGO_URL)) {

            MongoDatabase mongoDatabase =
                    mongoClient.getDatabase("liquibase_test");

            // Récupère les noms des collections présentes dans la base
            // et vérifie que la collection attendue figure dans la liste.
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