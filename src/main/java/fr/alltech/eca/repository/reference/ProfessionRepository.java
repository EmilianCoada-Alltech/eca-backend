package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Profession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessionRepository extends MongoRepository<Profession, String> {
}
