package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.ProfessionCategoryResponse;
import fr.alltech.eca.service.reference.ProfessionCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/references/profession-categories")
public class ProfessionCategoryController {

    private final ProfessionCategoryService professionCategoryService;

    public ProfessionCategoryController(
            ProfessionCategoryService professionCategoryService
    ) {
        this.professionCategoryService = professionCategoryService;
    }

    @GetMapping
    public List<ProfessionCategoryResponse> getAllProfessionCategories() {
        return professionCategoryService.getAllProfessionCategories();
    }
}