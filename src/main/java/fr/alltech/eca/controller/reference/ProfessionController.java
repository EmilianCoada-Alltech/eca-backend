package fr.alltech.eca.controller.reference;

import fr.alltech.eca.dto.reference.profession.ProfessionRequest;
import fr.alltech.eca.dto.reference.profession.ProfessionResponse;
import fr.alltech.eca.service.reference.ProfessionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/references/professions")
public class ProfessionController {

    private final ProfessionService professionService;

    public ProfessionController(ProfessionService professionService) {
        this.professionService = professionService;
    }

    @GetMapping("/active")
    public List<ProfessionResponse> getAllActiveProfessions() {
        return professionService.getAllActiveProfessions();
    }

    @GetMapping("/all")
    public List<ProfessionResponse> getAllProfessions() {
        return professionService.getAllProfessions();
    }

    @GetMapping("/{id}")
    public ProfessionResponse getProfessionById(@PathVariable String id) {
        return professionService.getProfessionById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfessionResponse createProfession(
            @RequestBody ProfessionRequest request
    ) {
        return professionService.createProfession(request);
    }

    @PutMapping("/{id}")
    public ProfessionResponse updateProfession(
            @PathVariable String id,
            @RequestBody ProfessionRequest request
    ) {
        return professionService.updateProfession(id, request);
    }
}