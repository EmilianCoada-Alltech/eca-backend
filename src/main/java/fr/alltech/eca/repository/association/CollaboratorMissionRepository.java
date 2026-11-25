package fr.alltech.eca.repository.association;

import fr.alltech.eca.model.association.CollaboratorMission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CollaboratorMissionRepository extends MongoRepository<CollaboratorMission, String> {
}