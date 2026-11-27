package fr.alltech.eca.dto.reference;

/**
 * Représente les données d'une catégorie de métier retournées par l'API.
 *
 * @param id          identifiant de la catégorie
 * @param name        nom de la catégorie
 * @param description description de la catégorie
 */
public record ProfessionCategoryResponse(
        String id,
        String name,
        String description
) {
}