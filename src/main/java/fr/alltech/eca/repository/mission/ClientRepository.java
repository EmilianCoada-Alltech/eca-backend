package fr.alltech.eca.repository.mission;

import fr.alltech.eca.model.mission.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Fournit les opérations d'accès aux données des clients.
 */
public interface ClientRepository extends MongoRepository<Client, String> {
}