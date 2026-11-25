package fr.alltech.eca.repository.mission;

import fr.alltech.eca.model.mission.Mission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MissionRepository extends MongoRepository<Mission, String> {
}