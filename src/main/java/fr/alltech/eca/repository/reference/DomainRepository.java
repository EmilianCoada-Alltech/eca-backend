package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Domain;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données des domaines.
 */
public interface DomainRepository extends MongoRepository<Domain, String> {
}
