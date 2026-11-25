package fr.alltech.eca.repository.collaborator;

import fr.alltech.eca.model.collaborator.Collaborator;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CollaboratorRepository extends MongoRepository<Collaborator, String> {
}
