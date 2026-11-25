package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
}
