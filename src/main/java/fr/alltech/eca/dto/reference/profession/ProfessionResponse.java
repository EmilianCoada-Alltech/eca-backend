package fr.alltech.eca.dto.reference.profession;

public record ProfessionResponse(
        String id,
        String name,
        String description,
        String categoryId,
        boolean active
) {
}