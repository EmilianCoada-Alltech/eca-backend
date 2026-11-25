package fr.alltech.eca.repository;

import fr.alltech.eca.model.ProfessionCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessionCategoryRepository extends MongoRepository<ProfessionCategory, String> {
}
