
package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.profession.ProfessionRequest;
import fr.alltech.eca.dto.reference.profession.ProfessionResponse;
import fr.alltech.eca.service.reference.ProfessionService;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        when(professionService.getAllActiveProfessions())
                .thenReturn(List.of(profession));

        mockMvc.perform(get("/api/references/professions/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("PROF-001"))
                .andExpect(jsonPath("$[0].name").value("Backend developer"))
                .andExpect(jsonPath("$[0].categoryId").value("CAT-01"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void shouldReturnEmptyArrayWhenNoActiveProfessionsExist() throws Exception {
        when(professionService.getAllActiveProfessions())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/references/professions/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnAllProfessionsIncludingInactiveOnes() throws Exception {
        ProfessionResponse activeProfession = new ProfessionResponse(
                "PROF-001",
                "Backend developer",
                "Develops backend services.",
                "CAT-01",
                true
        );

        ProfessionResponse inactiveProfession = new ProfessionResponse(
                "PROF-002",
                "Legacy developer",
                "An inactive profession.",
                "CAT-01",
                false
        );

        when(professionService.getAllProfessions())
                .thenReturn(List.of(activeProfession, inactiveProfession));

        mockMvc.perform(get("/api/references/professions/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value("PROF-002"))
                .andExpect(jsonPath("$[1].active").value(false));
    }


    @Test
    void shouldReturnProfessionById() throws Exception {
        ProfessionResponse profession = new ProfessionResponse(
                "PROF-003",
                "Backend developer",
                "Develops backend services.",
                "CAT-01",
                false
        );

        when(professionService.getProfessionById("PROF-003"))
                .thenReturn(profession);

        mockMvc.perform(get("/api/references/professions/PROF-003"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PROF-003"))
                .andExpect(jsonPath("$.name").value("Backend developer"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void shouldReturn404WhenProfessionDoesNotExist() throws Exception {
        when(professionService.getProfessionById("PROF-999"))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: PROF-999"
                ));

        mockMvc.perform(get("/api/references/professions/PROF-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateProfession() throws Exception {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "Develops backend services.",
                "CAT-001"
        );

        ProfessionResponse response = new ProfessionResponse(
                "PROF-063",
                "Backend developer",
                "Develops backend services.",
                "CAT-001",
                true
        );

        when(professionService.createProfession(request))
                .thenReturn(response);

        mockMvc.perform(post("/api/references/professions")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                                {
                                    "name": "Backend developer",
                                    "description": "Develops backend services.",
                                    "categoryId": "CAT-001"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id").value("PROF-063"))
                .andExpect(jsonPath("$.name").value("Backend developer"))
                .andExpect(jsonPath("$.categoryId").value("CAT-001"))
                .andExpect(jsonPath("$.active").value(true));

        verify(professionService).createProfession(request);
    }

    @Test
    void shouldReturn404WhenCreatingProfessionWithNonexistentCategory() throws Exception {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "Develops backend services.",
                "CAT-999"
        );

        when(professionService.createProfession(request))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession category not found with id: CAT-999"
                ));

        mockMvc.perform(post("/api/references/professions")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                                {
                                    "name": "Backend developer",
                                    "description": "Develops backend services.",
                                    "categoryId": "CAT-999"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(professionService).createProfession(request);
    }

    @Test
    void shouldUpdateProfession() throws Exception {
        ProfessionRequest request = new ProfessionRequest(
                "Senior backend developer",
                "Develops complex backend services.",
                "CAT-002"
        );

        ProfessionResponse response = new ProfessionResponse(
                "PROF-001",
                "Senior backend developer",
                "Develops complex backend services.",
                "CAT-002",
                true
        );

        when(professionService.updateProfession("PROF-001", request))
                .thenReturn(response);

        mockMvc.perform(put("/api/references/professions/PROF-001")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                                {
                                    "name": "Senior backend developer",
                                    "description": "Develops complex backend services.",
                                    "categoryId": "CAT-002"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id").value("PROF-001"))
                .andExpect(jsonPath("$.name").value("Senior backend developer"))
                .andExpect(jsonPath("$.description").value("Develops complex backend services."))
                .andExpect(jsonPath("$.categoryId").value("CAT-002"))
                .andExpect(jsonPath("$.active").value(true));

        verify(professionService).updateProfession("PROF-001", request);
    }

    @Test
    void shouldReturn404WhenUpdatingNonexistentProfession() throws Exception {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "New description.",
                "CAT-001"
        );

        when(professionService.updateProfession("PROF-999", request))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: PROF-999"
                ));

        mockMvc.perform(put("/api/references/professions/PROF-999")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                                {
                                    "name": "Backend developer",
                                    "description": "New description.",
                                    "categoryId": "CAT-001"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(professionService).updateProfession("PROF-999", request);
    }

    @Test
    void shouldReturn404WhenUpdatingWithNonexistentCategory() throws Exception {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "New description.",
                "CAT-999"
        );

        when(professionService.updateProfession("PROF-001", request))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession category not found with id: CAT-999"
                ));

        mockMvc.perform(put("/api/references/professions/PROF-001")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                                {
                                    "name": "Backend developer",
                                    "description": "New description.",
                                    "categoryId": "CAT-999"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(professionService).updateProfession("PROF-001", request);
    }
}
