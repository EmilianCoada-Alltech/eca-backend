package fr.alltech.eca.repository;

import fr.alltech.eca.model.CompetenceDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompetenceDomainRepository extends MongoRepository<CompetenceDomain, String> {
}