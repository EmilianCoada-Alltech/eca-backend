package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.ProfessionCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Fournit les opérations d'accès aux données des catégories de métiers.
 */
public interface ProfessionCategoryRepository extends MongoRepository<ProfessionCategory, String> {

    List<ProfessionCategory> findByActiveTrue();

    List<ProfessionCategory> findByActiveFalse();
}
