package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.profession.ProfessionRequest;
import fr.alltech.eca.dto.reference.profession.ProfessionResponse;
import fr.alltech.eca.service.reference.ProfessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfessionController.class)
class ProfessionControllerTest {

    private static final String BASE_URL = "/api/references/professions";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfessionService professionService;

    // -------------------------------------------------------------------------
    // GET /active
    // -------------------------------------------------------------------------

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

        mockMvc.perform(get(BASE_URL + "/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("PROF-001"))
                .andExpect(jsonPath("$[0].name").value("Backend developer"))
                .andExpect(jsonPath("$[0].description").value("Develops backend services."))
                .andExpect(jsonPath("$[0].categoryId").value("CAT-01"))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(professionService).getAllActiveProfessions();
    }

    @Test
    void shouldReturnMultipleActiveProfessions() throws Exception {
        ProfessionResponse first = new ProfessionResponse(
                "PROF-001", "Backend developer", "Develops backend services.", "CAT-01", true
        );

        ProfessionResponse second = new ProfessionResponse(
                "PROF-002", "Frontend developer", "Develops frontend applications.", "CAT-01", true
        );

        when(professionService.getAllActiveProfessions())
                .thenReturn(List.of(first, second));

        mockMvc.perform(get(BASE_URL + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("PROF-001"))
                .andExpect(jsonPath("$[1].id").value("PROF-002"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].active").value(true));

        verify(professionService).getAllActiveProfessions();
    }

    @Test
    void shouldReturnEmptyArrayWhenNoActiveProfessionsExist() throws Exception {
        when(professionService.getAllActiveProfessions())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(professionService).getAllActiveProfessions();
    }

    // -------------------------------------------------------------------------
    // GET /inactive
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnInactiveProfessions() throws Exception {
        ProfessionResponse profession = new ProfessionResponse(
                "PROF-001",
                "Backend developer",
                "Develops backend services.",
                "CAT-01",
                false
        );

        when(professionService.getAllInactiveProfessions())
                .thenReturn(List.of(profession));

        mockMvc.perform(get(BASE_URL + "/inactive"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("PROF-001"))
                .andExpect(jsonPath("$[0].name").value("Backend developer"))
                .andExpect(jsonPath("$[0].categoryId").value("CAT-01"))
                .andExpect(jsonPath("$[0].active").value(false));

        verify(professionService).getAllInactiveProfessions();
    }

    @Test
    void shouldReturnMultipleInactiveProfessions() throws Exception {
        ProfessionResponse first = new ProfessionResponse(
                "PROF-003", "Legacy developer", "Legacy profession.", "CAT-01", false
        );

        ProfessionResponse second = new ProfessionResponse(
                "PROF-004", "Deprecated analyst", "Deprecated profession.", "CAT-02", false
        );

        when(professionService.getAllInactiveProfessions())
                .thenReturn(List.of(first, second));

        mockMvc.perform(get(BASE_URL + "/inactive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].active").value(false))
                .andExpect(jsonPath("$[1].active").value(false));

        verify(professionService).getAllInactiveProfessions();
    }

    @Test
    void shouldReturnEmptyArrayWhenNoInactiveProfessionsExist() throws Exception {
        when(professionService.getAllInactiveProfessions())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/inactive"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(professionService).getAllInactiveProfessions();
    }

    // -------------------------------------------------------------------------
    // GET /all
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnAllProfessionsIncludingInactiveOnes() throws Exception {
        ProfessionResponse active = new ProfessionResponse(
                "PROF-001", "Backend developer", "Develops backend services.", "CAT-01", true
        );

        ProfessionResponse inactive = new ProfessionResponse(
                "PROF-002", "Legacy developer", "An inactive profession.", "CAT-01", false
        );

        when(professionService.getAllProfessions())
                .thenReturn(List.of(active, inactive));

        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("PROF-001"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value("PROF-002"))
                .andExpect(jsonPath("$[1].active").value(false));

        verify(professionService).getAllProfessions();
    }

    @Test
    void shouldReturnEmptyArrayWhenNoProfessionsExist() throws Exception {
        when(professionService.getAllProfessions())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(professionService).getAllProfessions();
    }

    // -------------------------------------------------------------------------
    // GET /{id}
    // -------------------------------------------------------------------------

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

        mockMvc.perform(get(BASE_URL + "/PROF-003"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("PROF-003"))
                .andExpect(jsonPath("$.name").value("Backend developer"))
                .andExpect(jsonPath("$.description").value("Develops backend services."))
                .andExpect(jsonPath("$.categoryId").value("CAT-01"))
                .andExpect(jsonPath("$.active").value(false));

        verify(professionService).getProfessionById("PROF-003");
    }

    @Test
    void shouldReturn404WhenProfessionDoesNotExist() throws Exception {
        when(professionService.getProfessionById("PROF-999"))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: PROF-999"
                ));

        mockMvc.perform(get(BASE_URL + "/PROF-999"))
                .andExpect(status().isNotFound());

        verify(professionService).getProfessionById("PROF-999");
    }

    // -------------------------------------------------------------------------
    // POST /
    // -------------------------------------------------------------------------

    @Test
    void shouldCreateProfession() throws Exception {
        ProfessionResponse response = new ProfessionResponse(
                "PROF-063",
                "Backend developer",
                "Develops backend services.",
                "CAT-001",
                true
        );

        when(professionService.createProfession(any(ProfessionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Backend developer",
                                    "description": "Develops backend services.",
                                    "categoryId": "CAT-001"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("PROF-063"))
                .andExpect(jsonPath("$.name").value("Backend developer"))
                .andExpect(jsonPath("$.description").value("Develops backend services."))
                .andExpect(jsonPath("$.categoryId").value("CAT-001"))
                .andExpect(jsonPath("$.active").value(true));

        verify(professionService).createProfession(any(ProfessionRequest.class));
    }

    @Test
    void shouldReturn404WhenCreatingProfessionWithNonexistentCategory() throws Exception {
        when(professionService.createProfession(any(ProfessionRequest.class)))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession category not found with id: CAT-999"
                ));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Backend developer",
                                    "description": "Develops backend services.",
                                    "categoryId": "CAT-999"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(professionService).createProfession(any(ProfessionRequest.class));
    }

    // -------------------------------------------------------------------------
    // PUT /{id}
    // -------------------------------------------------------------------------

    @Test
    void shouldUpdateProfession() throws Exception {
        ProfessionResponse response = new ProfessionResponse(
                "PROF-001",
                "Senior backend developer",
                "Develops complex backend services.",
                "CAT-002",
                true
        );

        when(professionService.updateProfession(
                eq("PROF-001"),
                any(ProfessionRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/PROF-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Senior backend developer",
                                    "description": "Develops complex backend services.",
                                    "categoryId": "CAT-002"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("PROF-001"))
                .andExpect(jsonPath("$.name").value("Senior backend developer"))
                .andExpect(jsonPath("$.description").value("Develops complex backend services."))
                .andExpect(jsonPath("$.categoryId").value("CAT-002"))
                .andExpect(jsonPath("$.active").value(true));

        verify(professionService).updateProfession(
                eq("PROF-001"),
                any(ProfessionRequest.class)
        );
    }

    @Test
    void shouldReturn404WhenUpdatingNonexistentProfession() throws Exception {
        when(professionService.updateProfession(
                eq("PROF-999"),
                any(ProfessionRequest.class)
        )).thenThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Profession not found with id: PROF-999"
        ));

        mockMvc.perform(put(BASE_URL + "/PROF-999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Backend developer",
                                    "description": "New description.",
                                    "categoryId": "CAT-001"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(professionService).updateProfession(
                eq("PROF-999"),
                any(ProfessionRequest.class)
        );
    }

    @Test
    void shouldReturn404WhenUpdatingWithNonexistentCategory() throws Exception {
        when(professionService.updateProfession(
                eq("PROF-001"),
                any(ProfessionRequest.class)
        )).thenThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Profession category not found with id: CAT-999"
        ));

        mockMvc.perform(put(BASE_URL + "/PROF-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Backend developer",
                                    "description": "New description.",
                                    "categoryId": "CAT-999"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(professionService).updateProfession(
                eq("PROF-001"),
                any(ProfessionRequest.class)
        );
    }

    // -------------------------------------------------------------------------
    // Service delegation
    // -------------------------------------------------------------------------

    @Test
    void shouldCallOnlyActiveProfessionServiceMethodForActiveEndpoint() throws Exception {
        when(professionService.getAllActiveProfessions()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/active"))
                .andExpect(status().isOk());

        verify(professionService).getAllActiveProfessions();
        verify(professionService, never()).getAllProfessions();
        verify(professionService, never()).getAllInactiveProfessions();
    }

    @Test
    void shouldCallOnlyInactiveProfessionServiceMethodForInactiveEndpoint() throws Exception {
        when(professionService.getAllInactiveProfessions()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/inactive"))
                .andExpect(status().isOk());

        verify(professionService).getAllInactiveProfessions();
        verify(professionService, never()).getAllProfessions();
        verify(professionService, never()).getAllActiveProfessions();
    }

    @Test
    void shouldCallOnlyAllProfessionsServiceMethodForAllEndpoint() throws Exception {
        when(professionService.getAllProfessions()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk());

        verify(professionService).getAllProfessions();
        verify(professionService, never()).getAllActiveProfessions();
        verify(professionService, never()).getAllInactiveProfessions();
    }
}