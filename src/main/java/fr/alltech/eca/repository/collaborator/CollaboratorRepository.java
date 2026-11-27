package fr.alltech.eca.repository.collaborator;

import fr.alltech.eca.model.collaborator.Collaborator;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Fournit les opérations d'accès aux données des collaborateurs.
 */
public interface CollaboratorRepository extends MongoRepository<Collaborator, String> {

    /**
     * Récupère les collaborateurs dont le profil est accessible à la recherche.
     *
     * @return la liste des collaborateurs recherchables
     */
    List<Collaborator> findBySearchableTrue();
}
