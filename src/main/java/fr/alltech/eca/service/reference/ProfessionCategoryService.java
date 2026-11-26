package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.ProfessionCategoryResponse;
import fr.alltech.eca.mapper.reference.ProfessionCategoryMapper;
import fr.alltech.eca.repository.reference.ProfessionCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<ProfessionCategoryResponse> getAllProfessionCategories() {
        return professionCategoryRepository.findAll()
                .stream()
                .map(professionCategoryMapper::toResponse)
                .toList();
    }
}