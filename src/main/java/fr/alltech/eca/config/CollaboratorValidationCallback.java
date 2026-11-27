package fr.alltech.eca.config;


import fr.alltech.eca.model.collaborator.Collaborator;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

/**
 * Valide les données d'un collaborateur avant leur conversion
 * en document MongoDB.
 */
@Component
public class CollaboratorValidationCallback
        implements BeforeConvertCallback<Collaborator> {

    @Override
    public @NonNull Collaborator onBeforeConvert(
            @NonNull Collaborator collaborator,
            @NonNull String collection
    ) {
        collaborator.validate();
        return collaborator;
    }
}