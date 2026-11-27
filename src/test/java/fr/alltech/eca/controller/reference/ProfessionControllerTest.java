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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests des endpoints du contrôleur de gestion des métiers.
 * Vérifie les réponses HTTP, les données JSON retournées et la bonne
 * délégation des opérations au service.
 */
@WebMvcTest(ProfessionController.class)
class ProfessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfessionService professionService;

    /**
     * Vérifie que l'API retourne uniquement les métiers actifs.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldReturnActiveProfessions() throws Exception {
        ProfessionResponse profession = new ProfessionResponse(
                "PROF-001",
                "Backend developer",
                "Develops backend services.",
                "CAT-01",
                true
        );

        // Simule le retour du service avec un métier actif.
        when(professionService.getAllActiveProfessions())
                .thenReturn(List.of(profession));

        // Vérifie le statut HTTP, le format JSON et les principales données retournées.
        mockMvc.perform(get("/api/references/professions/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("PROF-001"))
                .andExpect(jsonPath("$[0].name").value("Backend developer"))
                .andExpect(jsonPath("$[0].categoryId").value("CAT-01"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    /**
     * Vérifie que l'API retourne un tableau vide lorsqu'aucun métier actif
     * n'est disponible.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldReturnEmptyArrayWhenNoActiveProfessionsExist() throws Exception {
        // Simule un service ne retournant aucun métier actif.
        when(professionService.getAllActiveProfessions())
                .thenReturn(List.of());

        // Vérifie que l'API répond avec un statut 200 et un tableau JSON vide.
        mockMvc.perform(get("/api/references/professions/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Vérifie que l'API retourne tous les métiers, y compris ceux qui sont inactifs.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
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

        // Simule un résultat contenant un métier actif et un métier inactif.
        when(professionService.getAllProfessions())
                .thenReturn(List.of(activeProfession, inactiveProfession));

        // Vérifie que les deux métiers sont présents et que leur statut est conservé.
        mockMvc.perform(get("/api/references/professions/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value("PROF-002"))
                .andExpect(jsonPath("$[1].active").value(false));
    }

    /**
     * Vérifie la récupération d'un métier à partir de son identifiant.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldReturnProfessionById() throws Exception {
        ProfessionResponse profession = new ProfessionResponse(
                "PROF-003",
                "Backend developer",
                "Develops backend services.",
                "CAT-01",
                false
        );

        // Simule la récupération du métier demandé.
        when(professionService.getProfessionById("PROF-003"))
                .thenReturn(profession);

        // Vérifie que l'API retourne les données du métier correspondant à l'identifiant.
        mockMvc.perform(get("/api/references/professions/PROF-003"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PROF-003"))
                .andExpect(jsonPath("$.name").value("Backend developer"))
                .andExpect(jsonPath("$.active").value(false));
    }

    /**
     * Vérifie que l'API retourne une erreur 404 lorsque le métier demandé
     * n'existe pas.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldReturn404WhenProfessionDoesNotExist() throws Exception {
        // Simule l'erreur renvoyée par le service lorsque le métier est introuvable.
        when(professionService.getProfessionById("PROF-999"))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: PROF-999"
                ));

        // Vérifie que l'erreur du service est exposée avec le statut HTTP 404.
        mockMvc.perform(get("/api/references/professions/PROF-999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Vérifie la création d'un métier et la réponse retournée par l'API.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
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

        // Simule la création du métier et le retour de l'objet créé.
        when(professionService.createProfession(request))
                .thenReturn(response);

        // Envoie une requête POST avec les données du nouveau métier.
        mockMvc.perform(post("/api/references/professions")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                                {
                                    "name": "Backend developer",
                                    "description": "Develops backend services.",
                                    "categoryId": "CAT-001"
                                }
                                """))
                // Vérifie le statut 201 et les données du métier créé.
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id").value("PROF-063"))
                .andExpect(jsonPath("$.name").value("Backend developer"))
                .andExpect(jsonPath("$.categoryId").value("CAT-001"))
                .andExpect(jsonPath("$.active").value(true));

        // Vérifie que le contrôleur transmet la requête au service.
        verify(professionService).createProfession(request);
    }

    /**
     * Vérifie que la création d'un métier échoue avec une erreur 404
     * lorsque la catégorie indiquée n'existe pas.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldReturn404WhenCreatingProfessionWithNonexistentCategory() throws Exception {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "Develops backend services.",
                "CAT-999"
        );

        // Simule l'erreur du service lorsque la catégorie de métier est introuvable.
        when(professionService.createProfession(request))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession category not found with id: CAT-999"
                ));

        // Envoie une requête de création avec un identifiant de catégorie inexistant.
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

        // Vérifie que la demande de création a bien été transmise au service.
        verify(professionService).createProfession(request);
    }

    /**
     * Vérifie la modification d'un métier existant.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
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

        // Simule la modification du métier et retourne les données mises à jour.
        when(professionService.updateProfession("PROF-001", request))
                .thenReturn(response);

        // Envoie une requête PUT contenant les nouvelles données du métier.
        mockMvc.perform(put("/api/references/professions/PROF-001")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                                {
                                    "name": "Senior backend developer",
                                    "description": "Develops complex backend services.",
                                    "categoryId": "CAT-002"
                                }
                                """))
                // Vérifie le statut HTTP et les données mises à jour.
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.id").value("PROF-001"))
                .andExpect(jsonPath("$.name").value("Senior backend developer"))
                .andExpect(jsonPath("$.description").value("Develops complex backend services."))
                .andExpect(jsonPath("$.categoryId").value("CAT-002"))
                .andExpect(jsonPath("$.active").value(true));

        // Vérifie que l'identifiant et les nouvelles données sont transmis au service.
        verify(professionService).updateProfession("PROF-001", request);
    }

    /**
     * Vérifie que la modification d'un métier inexistant retourne une erreur 404.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldReturn404WhenUpdatingNonexistentProfession() throws Exception {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "New description.",
                "CAT-001"
        );

        // Simule l'erreur du service lorsque le métier à modifier est introuvable.
        when(professionService.updateProfession("PROF-999", request))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: PROF-999"
                ));

        // Envoie une requête de modification pour un identifiant inexistant.
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

        // Vérifie que la demande de modification a bien été transmise au service.
        verify(professionService).updateProfession("PROF-999", request);
    }

    /**
     * Vérifie que la modification d'un métier échoue avec une erreur 404
     * lorsque la nouvelle catégorie indiquée n'existe pas.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldReturn404WhenUpdatingWithNonexistentCategory() throws Exception {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "New description.",
                "CAT-999"
        );

        // Simule l'erreur du service lorsque la nouvelle catégorie est introuvable.
        when(professionService.updateProfession("PROF-001", request))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession category not found with id: CAT-999"
                ));

        // Envoie une requête de modification avec un identifiant de catégorie inexistant.
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

        // Vérifie que la demande de modification a bien été transmise au service.
        verify(professionService).updateProfession("PROF-001", request);
    }

    /**
     * Vérifie la désactivation d'un métier existant.
     * L'opération est une désactivation logique et non une suppression physique.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldDeactivateProfession() throws Exception {

        // Envoie une requête DELETE pour désactiver le métier.
        mockMvc.perform(delete("/api/references/professions/PROF-001"))
                .andExpect(status().isNoContent());

        // Vérifie que le service a reçu la demande de désactivation.
        verify(professionService).deactivateProfession("PROF-001");
    }

    /**
     * Vérifie que la désactivation d'un métier inexistant retourne une erreur 404.
     *
     * @throws Exception en cas d'erreur lors de l'exécution de la requête HTTP
     */
    @Test
    void shouldReturn404WhenDeactivatingNonexistentProfession() throws Exception {

        // Simule l'erreur du service lorsque le métier à désactiver est introuvable.
        doThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Profession not found with id: PROF-999"
        )).when(professionService).deactivateProfession("PROF-999");

        // Envoie une requête DELETE pour un identifiant inexistant.
        mockMvc.perform(delete("/api/references/professions/PROF-999"))
                .andExpect(status().isNotFound());

        // Vérifie que la demande de désactivation a bien été transmise au service.
        verify(professionService).deactivateProfession("PROF-999");
    }
}