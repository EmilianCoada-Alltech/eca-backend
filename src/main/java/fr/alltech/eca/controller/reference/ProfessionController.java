package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.ProfessionResponse;
import fr.alltech.eca.service.reference.ProfessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/references/professions")
public class ProfessionController {

    private final ProfessionService professionService;

    public ProfessionController(ProfessionService professionService) {
        this.professionService = professionService;
    }

    @GetMapping
    public List<ProfessionResponse> getAllProfessions() {
        return professionService.getAllProfessions();
    }
}