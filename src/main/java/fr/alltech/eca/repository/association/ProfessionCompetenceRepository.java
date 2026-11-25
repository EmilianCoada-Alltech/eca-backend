package fr.alltech.eca.repository.association;

import fr.alltech.eca.model.association.ProfessionCompetence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessionCompetenceRepository extends MongoRepository<ProfessionCompetence, String> {
}