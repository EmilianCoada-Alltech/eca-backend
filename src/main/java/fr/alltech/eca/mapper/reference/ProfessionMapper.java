package fr.alltech.eca.mapper.reference;

import fr.alltech.eca.dto.reference.profession.ProfessionResponse;
import fr.alltech.eca.model.reference.Profession;
import org.springframework.stereotype.Component;

@Component
public class ProfessionMapper {

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