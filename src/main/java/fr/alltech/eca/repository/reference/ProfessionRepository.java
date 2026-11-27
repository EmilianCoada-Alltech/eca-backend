package fr.alltech.eca.repository.reference;

import fr.alltech.eca.model.reference.Profession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Fournit les opérations d'accès aux données des métiers.
 */
public interface ProfessionRepository extends MongoRepository<Profession, String> {

    /**
     * Récupère uniquement les métiers actifs.
     *
     * @return la liste des métiers actifs
     */
    List<Profession> findByActiveTrue();

    /**
     * Récupère uniquement les métiers inactifs
     *
     * @return la liste des métiers inactifs
     */
    List<Profession> findByActiveFalse();
}
