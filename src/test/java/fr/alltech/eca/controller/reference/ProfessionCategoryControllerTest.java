package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.profession_category.ProfessionCategoryResponse;
import fr.alltech.eca.service.reference.ProfessionCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests des endpoints du contrôleur de consultation des catégories de métiers.
 * Vérifie les réponses HTTP et le contenu JSON retourné par l'API.
 */
@WebMvcTest(ProfessionCategoryController.class)
class ProfessionCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfessionCategoryService professionCategoryService;

    /**
     * Vérifie que l'API retourne correctement la liste des catégories de métiers
     * lorsque le service contient des résultats.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldReturnProfessionCategories() throws Exception {
        ProfessionCategoryResponse category = new ProfessionCategoryResponse(
                "CAT-01",
                "Development",
                "Groups professions related to software development.",
                true
        );

        // Simule le retour du service avec une catégorie de métier.
        when(professionCategoryService.getAllProfessionCategories())
                .thenReturn(List.of(category));

        // Vérifie le statut HTTP, le type de contenu et les données retournées.
        mockMvc.perform(get("/api/references/profession-categories/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("CAT-01"))
                .andExpect(jsonPath("$[0].name").value("Development"))
                .andExpect(jsonPath("$[0].description")
                        .value("Groups professions related to software development."));
    }

    /**
     * Vérifie que l'API retourne un tableau JSON vide lorsque le service
     * ne trouve aucune catégorie de métier.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldReturnEmptyArrayWhenNoProfessionCategoriesExist() throws Exception {
        // Simule un service ne retournant aucune catégorie.
        when(professionCategoryService.getAllProfessionCategories())
                .thenReturn(List.of());

        // Vérifie que l'API répond avec un statut 200 et un tableau vide.
        mockMvc.perform(get("/api/references/profession-categories/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnActiveProfessionCategories() throws Exception {
        ProfessionCategoryResponse category = new ProfessionCategoryResponse(
                "CAT-001",
                "Développement logiciel",
                "Conception, développement et maintenance de logiciels et applications.",
                true
        );

        when(professionCategoryService.getAllActiveProfessionCategories())
                .thenReturn(List.of(category));

        mockMvc.perform(get("/api/references/profession-categories/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("CAT-001"))
                .andExpect(jsonPath("$[0].name").value("Développement logiciel"))
                .andExpect(jsonPath("$[0].description").value("Conception, développement et maintenance de logiciels et applications."))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void shouldReturnInactiveActiveProfessionCategories() throws Exception {
        ProfessionCategoryResponse category = new ProfessionCategoryResponse(
                "CAT-001",
                "Développement logiciel",
                "Conception, développement et maintenance de logiciels et applications.",
                false
        );

        when(professionCategoryService.getAllInactiveProfessionCategories())
                .thenReturn(List.of(category));

        mockMvc.perform(get("/api/references/profession-categories/inactive"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("CAT-001"))
                .andExpect(jsonPath("$[0].name").value("Développement logiciel"))
                .andExpect(jsonPath("$[0].description").value("Conception, développement et maintenance de logiciels et applications."))
                .andExpect(jsonPath("$[0].active").value(false));
    }


}