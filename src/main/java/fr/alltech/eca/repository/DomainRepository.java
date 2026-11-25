package fr.alltech.eca.repository;

import fr.alltech.eca.model.Domain;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DomainRepository extends MongoRepository<Domain, String> {
    
}
