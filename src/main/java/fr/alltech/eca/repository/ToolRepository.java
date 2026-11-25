package fr.alltech.eca.repository;

import fr.alltech.eca.model.Tool;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ToolRepository extends MongoRepository<Tool, String> {
}
