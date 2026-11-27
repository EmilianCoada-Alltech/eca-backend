package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.profession_category.ProfessionCategoryRequest;
import fr.alltech.eca.dto.reference.profession_category.ProfessionCategoryResponse;
import fr.alltech.eca.service.reference.ProfessionCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfessionCategoryResponse createProfessionCategory(@RequestBody ProfessionCategoryRequest request) {
        return professionCategoryService.createProfessionCategory(request);
    }
}