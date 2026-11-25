package fr.alltech.eca.repository;

import fr.alltech.eca.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
}
