package fr.alltech.eca.repository;

import fr.alltech.eca.model.ProfessionCompetence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessionCompetenceRepository extends MongoRepository<ProfessionCompetence, String> {
}