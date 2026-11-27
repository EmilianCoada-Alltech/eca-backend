package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.ToolCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données des catégories d'outils.
 */
public interface ToolCategoryRepository extends MongoRepository<ToolCategory, String> {
}
