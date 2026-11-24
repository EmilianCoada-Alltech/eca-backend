package fr.alltech.eca.repository;

import fr.alltech.eca.model.Collaborator;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CollaboratorRepository extends MongoRepository<Collaborator, String> {

}
