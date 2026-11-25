package fr.alltech.eca.repository;

import fr.alltech.eca.model.ToolCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ToolCategoryRepository extends MongoRepository<ToolCategory, String> {
}
