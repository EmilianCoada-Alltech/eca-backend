package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données des rôles.
 */
public interface RoleRepository extends MongoRepository<Role, String> {
}
