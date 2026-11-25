package fr.alltech.eca.repository;

import fr.alltech.eca.model.Agency;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AgencyRepository extends MongoRepository<Agency, String> {
}