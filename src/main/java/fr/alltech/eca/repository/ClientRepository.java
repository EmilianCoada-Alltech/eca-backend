package fr.alltech.eca.repository;

import fr.alltech.eca.model.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository extends MongoRepository<Client, String> {
}