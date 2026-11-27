package fr.alltech.eca.repository.association;

import fr.alltech.eca.model.association.CompetenceDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données des associations
 * entre compétences et domaines.
 */
public interface CompetenceDomainRepository extends MongoRepository<CompetenceDomain, String> {
}