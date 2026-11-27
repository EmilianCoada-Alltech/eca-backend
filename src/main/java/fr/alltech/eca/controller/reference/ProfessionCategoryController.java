package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.ProfessionCategoryResponse;
import fr.alltech.eca.service.reference.ProfessionCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expose les endpoints de consultation des catégories de métiers.
 */
@RestController
@RequestMapping("/api/references/profession-categories")
public class ProfessionCategoryController {

    private final ProfessionCategoryService professionCategoryService;

    public ProfessionCategoryController(
            ProfessionCategoryService professionCategoryService
    ) {
        this.professionCategoryService = professionCategoryService;
    }

    /**
     * GET /api/references/profession-categories
     * <p>
     * Récupère l'ensemble des catégories de métiers.
     *
     * @return la liste des catégories de métiers
     */
    @GetMapping("/all")
    public List<ProfessionCategoryResponse> getAllProfessionCategories() {
        return professionCategoryService.getAllProfessionCategories();
    }

    @GetMapping("/active")
    public List<ProfessionCategoryResponse> getAllActiveProfessionCategories() {
        return professionCategoryService.getAllActiveProfessionCategories();
    }

    @GetMapping("/inactive")
    public List<ProfessionCategoryResponse> getAllInactiveProfessionCategories() {
        return professionCategoryService.getAllInactiveProfessionCategories();
    }

    @GetMapping("/{id}")
    public ProfessionCategoryResponse getProfessionCategoryById(@PathVariable String id) {
        return professionCategoryService.getProfessionCategoryById(id);
    }
}