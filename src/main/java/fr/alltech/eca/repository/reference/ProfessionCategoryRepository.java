package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.ProfessionCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessionCategoryRepository extends MongoRepository<ProfessionCategory, String> {
}
