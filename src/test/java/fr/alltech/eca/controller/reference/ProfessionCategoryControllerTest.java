package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.profession_category.ProfessionCategoryResponse;
import fr.alltech.eca.service.reference.ProfessionCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfessionCategoryController.class)
class ProfessionCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfessionCategoryService professionCategoryService;

    private static final String BASE_URL = "/api/references/profession-categories";

    // -------------------------------------------------------------------------
    // GET /all
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnProfessionCategories() throws Exception {
        ProfessionCategoryResponse category = new ProfessionCategoryResponse(
                "CAT-01",
                "Development",
                "Groups professions related to software development.",
                true
        );

        when(professionCategoryService.getAllProfessionCategories())
                .thenReturn(List.of(category));

        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("CAT-01"))
                .andExpect(jsonPath("$[0].name").value("Development"))
                .andExpect(jsonPath("$[0].description")
                        .value("Groups professions related to software development."))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(professionCategoryService).getAllProfessionCategories();
    }

    @Test
    void shouldReturnMultipleProfessionCategories() throws Exception {
        ProfessionCategoryResponse first = new ProfessionCategoryResponse(
                "CAT-01", "Development", "Software development.", true
        );

        ProfessionCategoryResponse second = new ProfessionCategoryResponse(
                "CAT-02", "Infrastructure", "Infrastructure and networks.", true
        );

        ProfessionCategoryResponse third = new ProfessionCategoryResponse(
                "CAT-03", "Administration", "Administrative professions.", false
        );

        when(professionCategoryService.getAllProfessionCategories())
                .thenReturn(List.of(first, second, third));

        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("CAT-01"))
                .andExpect(jsonPath("$[1].id").value("CAT-02"))
                .andExpect(jsonPath("$[2].id").value("CAT-03"))
                .andExpect(jsonPath("$[2].active").value(false));

        verify(professionCategoryService).getAllProfessionCategories();
    }

    @Test
    void shouldReturnEmptyArrayWhenNoProfessionCategoriesExist() throws Exception {
        when(professionCategoryService.getAllProfessionCategories())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(professionCategoryService).getAllProfessionCategories();
    }

    // -------------------------------------------------------------------------
    // GET /active
    // -------------------------------------------------------------------------

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

        mockMvc.perform(get(BASE_URL + "/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("CAT-001"))
                .andExpect(jsonPath("$[0].name").value("Développement logiciel"))
                .andExpect(jsonPath("$[0].description")
                        .value("Conception, développement et maintenance de logiciels et applications."))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(professionCategoryService).getAllActiveProfessionCategories();
    }

    @Test
    void shouldReturnMultipleActiveProfessionCategories() throws Exception {
        ProfessionCategoryResponse first = new ProfessionCategoryResponse(
                "CAT-001", "Development", "Software development.", true
        );

        ProfessionCategoryResponse second = new ProfessionCategoryResponse(
                "CAT-002", "Infrastructure", "Infrastructure and networks.", true
        );

        when(professionCategoryService.getAllActiveProfessionCategories())
                .thenReturn(List.of(first, second));

        mockMvc.perform(get(BASE_URL + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].active").value(true));

        verify(professionCategoryService).getAllActiveProfessionCategories();
    }

    @Test
    void shouldReturnEmptyArrayWhenNoActiveProfessionCategoriesExist() throws Exception {
        when(professionCategoryService.getAllActiveProfessionCategories())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(professionCategoryService).getAllActiveProfessionCategories();
    }

    // -------------------------------------------------------------------------
    // GET /inactive
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnInactiveProfessionCategories() throws Exception {
        ProfessionCategoryResponse category = new ProfessionCategoryResponse(
                "CAT-001",
                "Développement logiciel",
                "Conception, développement et maintenance de logiciels et applications.",
                false
        );

        when(professionCategoryService.getAllInactiveProfessionCategories())
                .thenReturn(List.of(category));

        mockMvc.perform(get(BASE_URL + "/inactive"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("CAT-001"))
                .andExpect(jsonPath("$[0].name").value("Développement logiciel"))
                .andExpect(jsonPath("$[0].description")
                        .value("Conception, développement et maintenance de logiciels et applications."))
                .andExpect(jsonPath("$[0].active").value(false));

        verify(professionCategoryService).getAllInactiveProfessionCategories();
    }

    @Test
    void shouldReturnMultipleInactiveProfessionCategories() throws Exception {
        ProfessionCategoryResponse first = new ProfessionCategoryResponse(
                "CAT-003", "Legacy", "Legacy professions.", false
        );

        ProfessionCategoryResponse second = new ProfessionCategoryResponse(
                "CAT-004", "Deprecated", "Deprecated professions.", false
        );

        when(professionCategoryService.getAllInactiveProfessionCategories())
                .thenReturn(List.of(first, second));

        mockMvc.perform(get(BASE_URL + "/inactive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].active").value(false))
                .andExpect(jsonPath("$[1].active").value(false));

        verify(professionCategoryService).getAllInactiveProfessionCategories();
    }

    @Test
    void shouldReturnEmptyArrayWhenNoInactiveProfessionCategoriesExist() throws Exception {
        when(professionCategoryService.getAllInactiveProfessionCategories())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/inactive"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(professionCategoryService).getAllInactiveProfessionCategories();
    }

    // -------------------------------------------------------------------------
    // Endpoint isolation
    // -------------------------------------------------------------------------

    @Test
    void shouldOnlyCallAllCategoriesServiceMethodForAllEndpoint() throws Exception {
        when(professionCategoryService.getAllProfessionCategories())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk());

        verify(professionCategoryService).getAllProfessionCategories();
        verify(professionCategoryService, never()).getAllActiveProfessionCategories();
        verify(professionCategoryService, never()).getAllInactiveProfessionCategories();
    }

    @Test
    void shouldOnlyCallActiveCategoriesServiceMethodForActiveEndpoint() throws Exception {
        when(professionCategoryService.getAllActiveProfessionCategories())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/active"))
                .andExpect(status().isOk());

        verify(professionCategoryService).getAllActiveProfessionCategories();
        verify(professionCategoryService, never()).getAllProfessionCategories();
        verify(professionCategoryService, never()).getAllInactiveProfessionCategories();
    }

    @Test
    void shouldOnlyCallInactiveCategoriesServiceMethodForInactiveEndpoint() throws Exception {
        when(professionCategoryService.getAllInactiveProfessionCategories())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/inactive"))
                .andExpect(status().isOk());

        verify(professionCategoryService).getAllInactiveProfessionCategories();
        verify(professionCategoryService, never()).getAllProfessionCategories();
        verify(professionCategoryService, never()).getAllActiveProfessionCategories();
    }
}