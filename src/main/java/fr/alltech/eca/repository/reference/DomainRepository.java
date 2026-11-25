package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Domain;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DomainRepository extends MongoRepository<Domain, String> {
}
