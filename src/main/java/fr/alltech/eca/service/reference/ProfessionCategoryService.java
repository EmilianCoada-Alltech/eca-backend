package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.ProfessionCategoryResponse;
import fr.alltech.eca.mapper.reference.ProfessionCategoryMapper;
import fr.alltech.eca.repository.reference.ProfessionCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Gère la consultation des catégories de métiers du référentiel ECA.
 */
@Service
public class ProfessionCategoryService {

    private final ProfessionCategoryRepository professionCategoryRepository;
    private final ProfessionCategoryMapper professionCategoryMapper;

    public ProfessionCategoryService(
            ProfessionCategoryRepository professionCategoryRepository,
            ProfessionCategoryMapper professionCategoryMapper
    ) {
        this.professionCategoryRepository = professionCategoryRepository;
        this.professionCategoryMapper = professionCategoryMapper;
    }

    /**
     * Récupère toutes les catégories de métiers et les convertit en DTO de réponse.
     *
     * @return la liste des catégories de métiers
     */
    public List<ProfessionCategoryResponse> getAllProfessionCategories() {
        System.out.println("ALL: " + professionCategoryRepository.findAll().size());
        System.out.println("ACTIVE: " + professionCategoryRepository.findByActiveTrue().size());
        System.out.println("INACTIVE: " + professionCategoryRepository.findByActiveFalse().size());
        return professionCategoryRepository.findAll()
                .stream()
                .map(professionCategoryMapper::toResponse)
                .toList();
    }

    public List<ProfessionCategoryResponse> getAllActiveProfessionCategories() {
        System.out.println("ALL: " + professionCategoryRepository.findAll().size());
        System.out.println("ACTIVE: " + professionCategoryRepository.findByActiveTrue().size());
        System.out.println("INACTIVE: " + professionCategoryRepository.findByActiveFalse().size());
        return professionCategoryRepository.findByActiveTrue()
                .stream()
                .map(professionCategoryMapper::toResponse)
                .toList();
    }

    public List<ProfessionCategoryResponse> getAllInactiveProfessionCategories() {
        return professionCategoryRepository.findByActiveFalse()
                .stream()
                .map(professionCategoryMapper::toResponse)
                .toList();
    }
}