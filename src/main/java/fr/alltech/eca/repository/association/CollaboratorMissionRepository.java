package fr.alltech.eca.repository.association;

import fr.alltech.eca.model.association.CollaboratorMission;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données des affectations
 * entre collaborateurs et missions.
 */
public interface CollaboratorMissionRepository extends MongoRepository<CollaboratorMission, String> {
}