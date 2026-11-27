package fr.alltech.eca.repository.mission;

import fr.alltech.eca.model.mission.Mission;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données des missions.
 */
public interface MissionRepository extends MongoRepository<Mission, String> {
}