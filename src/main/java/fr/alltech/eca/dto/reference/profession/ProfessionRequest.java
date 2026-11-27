package fr.alltech.eca.dto.reference.profession;

public record ProfessionRequest(
        String name,
        String description,
        String categoryId
) {
}
