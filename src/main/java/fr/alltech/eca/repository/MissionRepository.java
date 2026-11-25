package fr.alltech.eca.repository;

import fr.alltech.eca.model.Mission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MissionRepository extends MongoRepository<Mission, String> {
}