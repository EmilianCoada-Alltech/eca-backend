package fr.alltech.eca.mapper.reference;

import fr.alltech.eca.dto.reference.profession.ProfessionResponse;
import fr.alltech.eca.model.reference.Profession;
import org.springframework.stereotype.Component;

/**
 * Convertit les objets Profession en objets ProfessionResponse.
 */
@Component
public class ProfessionMapper {

    /**
     * Transforme un métier en DTO de réponse.
     *
     * @param profession métier à convertir
     * @return représentation du métier destinée à l'API
     */
    public ProfessionResponse toResponse(Profession profession) {
        return new ProfessionResponse(
                profession.getId(),
                profession.getName(),
                profession.getDescription(),
                profession.getCategoryId(),
                profession.isActive()
        );
    }
}