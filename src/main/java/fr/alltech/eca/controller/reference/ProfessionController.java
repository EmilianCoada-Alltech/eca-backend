package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.profession.ProfessionRequest;
import fr.alltech.eca.dto.reference.profession.ProfessionResponse;
import fr.alltech.eca.service.reference.ProfessionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expose les endpoints de gestion des métiers.
 */
@RestController
@RequestMapping("/api/references/professions")
public class ProfessionController {

    private final ProfessionService professionService;

    public ProfessionController(ProfessionService professionService) {
        this.professionService = professionService;
    }

    /**
     * GET /api/references/professions/active
     * <p>
     * Récupère uniquement les métiers actifs.
     *
     * @return la liste des métiers actifs
     */
    @GetMapping("/active")
    public List<ProfessionResponse> getAllActiveProfessions() {
        return professionService.getAllActiveProfessions();
    }

    /**
     * GET /api/references/professions/all
     * <p>
     * Récupère l'ensemble des métiers, actifs et inactifs.
     *
     * @return la liste de tous les métiers
     */
    @GetMapping("/all")
    public List<ProfessionResponse> getAllProfessions() {
        return professionService.getAllProfessions();
    }


    @GetMapping("/inactive")
    public List<ProfessionResponse> getAllInactiveProfessions() {
        return professionService.getAllInactiveProfessions();
    }

    /**
     * GET /api/references/professions/{id}
     * <p>
     * Récupère un métier à partir de son identifiant.
     *
     * @param id identifiant du métier
     * @return le métier correspondant
     */
    @GetMapping("/{id}")
    public ProfessionResponse getProfessionById(@PathVariable String id) {
        return professionService.getProfessionById(id);
    }

    /**
     * POST /api/references/professions
     * <p>
     * Crée un nouveau métier.
     *
     * @param request données du métier à créer
     * @return le métier créé
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfessionResponse createProfession(
            @RequestBody ProfessionRequest request
    ) {
        return professionService.createProfession(request);
    }

    /**
     * PUT /api/references/professions/{id}
     * <p>
     * Met à jour un métier existant.
     *
     * @param id      identifiant du métier à modifier
     * @param request nouvelles données du métier
     * @return le métier mis à jour
     */
    @PutMapping("/{id}")
    public ProfessionResponse updateProfession(
            @PathVariable String id,
            @RequestBody ProfessionRequest request
    ) {
        return professionService.updateProfession(id, request);
    }


    @PatchMapping("/{id}/toggle-status")
    public ProfessionResponse deactivateProfession(@PathVariable String id) {
        return professionService.toggleProfessionStatus(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfession(@PathVariable String id) {
        professionService.deleteProfession(id);
    }
}