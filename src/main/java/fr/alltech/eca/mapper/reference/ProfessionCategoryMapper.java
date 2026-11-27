package fr.alltech.eca.mapper.reference;

import fr.alltech.eca.dto.reference.profession_category.ProfessionCategoryResponse;
import fr.alltech.eca.model.reference.ProfessionCategory;
import org.springframework.stereotype.Component;

/**
 * Convertit les objets ProfessionCategory en objets ProfessionCategoryResponse.
 */
@Component
public class ProfessionCategoryMapper {

    /**
     * Transforme une catégorie de métier en DTO de réponse.
     *
     * @param category catégorie à convertir
     * @return représentation de la catégorie destinée à l'API
     */
    public ProfessionCategoryResponse toResponse(ProfessionCategory category) {
        return new ProfessionCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive()
        );
    }
}