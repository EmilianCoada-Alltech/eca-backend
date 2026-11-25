package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.ProfessionResponse;
import fr.alltech.eca.service.reference.ProfessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfessionController.class)
class ProfessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfessionService professionService;

    @Test
    void shouldReturnActiveProfessions() throws Exception {
        ProfessionResponse profession = new ProfessionResponse(
                "PROF-001",
                "Backend developer",
                "Develops backend services.",
                "CAT-01",
                true
        );

        when(professionService.getAllProfessions())
                .thenReturn(List.of(profession));

        mockMvc.perform(get("/api/references/professions"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("PROF-001"))
                .andExpect(jsonPath("$[0].name").value("Backend developer"))
                .andExpect(jsonPath("$[0].categoryId").value("CAT-01"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void shouldReturnEmptyArrayWhenNoProfessionsExist() throws Exception {
        when(professionService.getAllProfessions())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/references/professions"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(0));
    }
}