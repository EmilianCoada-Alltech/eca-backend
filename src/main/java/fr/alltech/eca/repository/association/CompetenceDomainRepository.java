package fr.alltech.eca.repository.association;

import fr.alltech.eca.model.association.CompetenceDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompetenceDomainRepository extends MongoRepository<CompetenceDomain, String> {
}