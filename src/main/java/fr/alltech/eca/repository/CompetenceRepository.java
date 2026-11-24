package fr.alltech.eca.repository;

import fr.alltech.eca.model.Competence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompetenceRepository extends MongoRepository<Competence, String> {

}