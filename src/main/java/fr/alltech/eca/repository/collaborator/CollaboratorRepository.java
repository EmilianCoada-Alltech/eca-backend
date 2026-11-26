package fr.alltech.eca.repository.collaborator;

import fr.alltech.eca.model.collaborator.Collaborator;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CollaboratorRepository extends MongoRepository<Collaborator, String> {

    List<Collaborator> findBySearchableTrue();
}
