package fr.alltech.eca.service.common;

import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class SequenceGenerator {

    private static final String COUNTER_COLLECTION = "database_sequences";

    private final MongoTemplate mongoTemplate;

    public SequenceGenerator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public String generate(String sequenceName, String prefix, int initialValue) {

        Query query = new Query(where("_id").is(sequenceName));

        // Initialize the sequence if it does not already exist.
        // setOnInsert only applies when MongoDB creates a new document.
        Update initialization = new Update()
                .setOnInsert("seq", initialValue);

        mongoTemplate.upsert(
                query,
                initialization,
                COUNTER_COLLECTION
        );

        // Increment the sequence atomically and retrieve the updated value.
        Update increment = new Update()
                .inc("seq", 1);

        FindAndModifyOptions options = FindAndModifyOptions.options()
                .returnNew(true);

        Document counter = mongoTemplate.findAndModify(
                query,
                increment,
                options,
                Document.class,
                COUNTER_COLLECTION
        );

        if (counter == null) {
            throw new IllegalStateException(
                    "Unable to generate ID for sequence: " + sequenceName
            );
        }

        Number sequence = (Number) counter.get("seq");

        return String.format("%s-%03d", prefix, sequence.intValue());
    }
}