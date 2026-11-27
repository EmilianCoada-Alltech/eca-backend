package fr.alltech.eca.dto.reference.profession;

/**
 * Représente les données nécessaires à la création ou à la modification d'un métier.
 *
 * @param name        nom du métier
 * @param description description du métier
 * @param categoryId  identifiant de la catégorie associée
 */
public record ProfessionRequest(
        String name,
        String description,
        String categoryId
) {
}
