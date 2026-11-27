package fr.alltech.eca.dto.reference.profession_category;

/**
 * Représente les données d'une catégorie de métier retournées par l'API.
 *
 * @param id          identifiant de la catégorie
 * @param name        nom de la catégorie
 * @param description description de la catégorie
 * @param active      indique si la catégorie est active
 */
public record ProfessionCategoryResponse(
        String id,
        String name,
        String description,
        boolean active
) {
}