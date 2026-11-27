package fr.alltech.eca.repository.association;

import fr.alltech.eca.model.association.ProfessionCompetence;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données des associations
 * entre métiers et compétences.
 */
public interface ProfessionCompetenceRepository extends MongoRepository<ProfessionCompetence, String> {
}