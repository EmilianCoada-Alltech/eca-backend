package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.ExperienceLevel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExperienceLevelRepository extends MongoRepository<ExperienceLevel, String> {
}