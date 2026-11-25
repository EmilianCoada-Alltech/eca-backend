package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Profession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProfessionRepository extends MongoRepository<Profession, String> {

    List<Profession> findByActiveTrue();
}
