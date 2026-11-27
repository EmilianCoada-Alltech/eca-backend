package fr.alltech.eca.service.common;

import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Génère des identifiants séquentiels uniques à partir de compteurs stockés
 * dans MongoDB.
 */
@Component
public class SequenceGenerator {

    private static final String COUNTER_COLLECTION = "database_sequences";

    private final MongoTemplate mongoTemplate;

    public SequenceGenerator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Génère un identifiant unique à partir d'une séquence stockée dans MongoDB.
     *
     * @param sequenceName nom de la séquence utilisée pour le compteur
     * @param prefix       préfixe associé au type d'entité
     * @param initialValue valeur de départ du compteur si la séquence n'existe pas
     * @return identifiant formaté sous la forme PREFIX-XXX
     * @throws IllegalStateException si le compteur ne peut pas être récupéré
     */
    public String generate(String sequenceName, String prefix, int initialValue) {

        // Construit le critère de recherche à partir du nom de la séquence.
        // Chaque séquence est identifiée par son champ MongoDB "_id".
        Query query = new Query(where("_id").is(sequenceName));

        // Initialise le compteur uniquement si le document n'existe pas encore.
        // setOnInsert évite d'écraser la valeur d'une séquence déjà initialisée.
        Update initialization = new Update()
                .setOnInsert("seq", initialValue);

        // Crée le document du compteur s'il est absent, sans modifier
        // le compteur lorsqu'il existe déjà.
        mongoTemplate.upsert(
                query,
                initialization,
                COUNTER_COLLECTION
        );

        // Prépare l'incrémentation du compteur de 1.
        Update increment = new Update()
                .inc("seq", 1);

        // Demande à MongoDB de retourner le document après sa modification,
        // afin de récupérer directement la nouvelle valeur du compteur.
        FindAndModifyOptions options = FindAndModifyOptions.options()
                .returnNew(true);

        // Incrémente le compteur de manière atomique.
        // Cette opération évite que plusieurs requêtes simultanées
        // récupèrent le même numéro.
        Document counter = mongoTemplate.findAndModify(
                query,
                increment,
                options,
                Document.class,
                COUNTER_COLLECTION
        );

        // Vérifie que MongoDB a bien retourné le document du compteur.
        if (counter == null) {
            throw new IllegalStateException(
                    "Unable to generate ID for sequence: " + sequenceName
            );
        }

        // Récupère la valeur numérique du compteur après incrémentation.
        Number sequence = (Number) counter.get("seq");

        // Formate l'identifiant avec son préfixe et un numéro sur trois chiffres.
        // Exemple : PROF-063.
        return String.format("%s-%03d", prefix, sequence.intValue());
    }
}