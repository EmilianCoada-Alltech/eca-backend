
package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.ProfessionCategoryResponse;
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

@WebMvcTest(ProfessionCategoryController.class)
class ProfessionCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfessionCategoryService professionCategoryService;

    @Test
    void shouldReturnProfessionCategories() throws Exception {
        ProfessionCategoryResponse category = new ProfessionCategoryResponse(
                "CAT-01",
                "Development",
                "Groups professions related to software development."
        );

        when(professionCategoryService.getAllProfessionCategories())
                .thenReturn(List.of(category));

        mockMvc.perform(get("/api/references/profession-categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("CAT-01"))
                .andExpect(jsonPath("$[0].name").value("Development"))
                .andExpect(jsonPath("$[0].description")
                        .value("Groups professions related to software development."));
    }

    @Test
    void shouldReturnEmptyArrayWhenNoProfessionCategoriesExist() throws Exception {
        when(professionCategoryService.getAllProfessionCategories())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/references/profession-categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(0));
    }
}
