package fr.alltech.eca.repository;

import fr.alltech.eca.model.CollaboratorMission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CollaboratorMissionRepository
        extends MongoRepository<CollaboratorMission, String> {
}