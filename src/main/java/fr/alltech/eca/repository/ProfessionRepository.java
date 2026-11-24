package fr.alltech.eca.repository;

import fr.alltech.eca.model.Profession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessionRepository extends MongoRepository<Profession, String> {

}
