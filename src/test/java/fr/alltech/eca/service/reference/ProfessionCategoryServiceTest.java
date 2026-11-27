package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.ProfessionCategoryResponse;
import fr.alltech.eca.mapper.reference.ProfessionCategoryMapper;
import fr.alltech.eca.model.reference.ProfessionCategory;
import fr.alltech.eca.repository.reference.ProfessionCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires du service ProfessionCategoryService.
 * Vérifie la récupération des catégories de professions,
 * leur transformation en DTO et le comportement lorsque
 * le repository ne retourne aucune donnée.
 */
@ExtendWith(MockitoExtension.class)
class ProfessionCategoryServiceTest {

    @Mock
    private ProfessionCategoryRepository professionCategoryRepository;

    private ProfessionCategoryService professionCategoryService;

    /**
     * Initialise le service avec un repository simulé
     * et une instance réelle du mapper.
     */
    @BeforeEach
    void setUp() {
        professionCategoryService = new ProfessionCategoryService(
                professionCategoryRepository,
                new ProfessionCategoryMapper()
        );
    }

    /**
     * Vérifie que les catégories récupérées depuis le repository
     * sont correctement converties en réponses DTO.
     */
    @Test
    void shouldReturnProfessionCategoriesAsResponses() {
        // Préparation : crée une catégorie qui sera retournée par le repository.
        ProfessionCategory category = new ProfessionCategory(
                "Development",
                "Groups professions related to software development.",
                true

        );

        // Simulation : configure le repository pour retourner cette catégorie.
        when(professionCategoryRepository.findAll())
                .thenReturn(List.of(category));

        // Exécution : récupère les catégories via le service.
        List<ProfessionCategoryResponse> result =
                professionCategoryService.getAllProfessionCategories();

        // Vérification : confirme qu'une seule catégorie est retournée.
        assertThat(result).hasSize(1);

        ProfessionCategoryResponse response = result.get(0);

        // Vérification : contrôle les données du DTO obtenu après transformation.
        assertThat(response.name()).isEqualTo("Development");
        assertThat(response.description())
                .isEqualTo("Groups professions related to software development.");

        // Vérification : confirme que le service a bien interrogé le repository.
        verify(professionCategoryRepository).findAll();
    }

    /**
     * Vérifie que le service retourne une liste vide
     * lorsqu'aucune catégorie de profession n'est disponible.
     */
    @Test
    void shouldReturnEmptyListWhenNoProfessionCategoriesExist() {
        // Simulation : configure le repository pour retourner une liste vide.
        when(professionCategoryRepository.findAll())
                .thenReturn(List.of());

        // Exécution : récupère les catégories via le service.
        List<ProfessionCategoryResponse> result =
                professionCategoryService.getAllProfessionCategories();

        // Vérification : confirme que le résultat est une liste vide.
        assertThat(result).isEmpty();

        // Vérification : confirme que le service a bien interrogé le repository.
        verify(professionCategoryRepository).findAll();
    }
}