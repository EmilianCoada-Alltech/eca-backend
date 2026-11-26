package fr.alltech.eca.mapper.reference;

import fr.alltech.eca.dto.reference.ProfessionCategoryResponse;
import fr.alltech.eca.model.reference.ProfessionCategory;
import org.springframework.stereotype.Component;

@Component
public class ProfessionCategoryMapper {

    public ProfessionCategoryResponse toResponse(ProfessionCategory category) {
        return new ProfessionCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}