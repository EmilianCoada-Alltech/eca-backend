package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.ToolCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ToolCategoryRepository extends MongoRepository<ToolCategory, String> {
}
