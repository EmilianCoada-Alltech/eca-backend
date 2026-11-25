package fr.alltech.eca.repository.tool;

import fr.alltech.eca.model.tool.Tool;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ToolRepository extends MongoRepository<Tool, String> {
}
