package fr.alltech.eca.dto.reference.profession;

/**
 * Représente les données d'un métier retournées par l'API.
 *
 * @param id          identifiant du métier
 * @param name        nom du métier
 * @param description description du métier
 * @param categoryId  identifiant de la catégorie associée
 * @param active      indique si le métier est actif
 */
public record ProfessionResponse(
        String id,
        String name,
        String description,
        String categoryId,
        boolean active
) {
}