package fr.alltech.eca.dto.reference;

public record ProfessionResponse(
        String id,
        String name,
        String description,
        String categoryId,
        boolean active
) {
}