package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Agency;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données des agences.
 */
public interface AgencyRepository extends MongoRepository<Agency, String> {
}