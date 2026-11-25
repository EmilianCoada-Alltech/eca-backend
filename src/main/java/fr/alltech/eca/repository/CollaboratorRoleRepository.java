package fr.alltech.eca.repository;

import fr.alltech.eca.model.CollaboratorRole;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CollaboratorRoleRepository extends MongoRepository<CollaboratorRole, String> {
}
