package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Competence;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données des compétences.
 */
public interface CompetenceRepository extends MongoRepository<Competence, String> {
}