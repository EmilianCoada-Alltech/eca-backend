package fr.alltech.eca.repository;

import fr.alltech.eca.model.ExperienceLevel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExperienceLevelRepository extends MongoRepository<ExperienceLevel, String> {
}