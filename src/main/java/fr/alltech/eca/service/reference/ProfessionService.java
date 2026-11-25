package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.ProfessionResponse;
import fr.alltech.eca.mapper.reference.ProfessionMapper;
import fr.alltech.eca.repository.reference.ProfessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessionService {

    private final ProfessionRepository professionRepository;
    private final ProfessionMapper professionMapper;

    public ProfessionService(
            ProfessionRepository professionRepository,
            ProfessionMapper professionMapper
    ) {
        this.professionRepository = professionRepository;
        this.professionMapper = professionMapper;
    }

    public List<ProfessionResponse> getAllProfessions() {
        return professionRepository.findByActiveTrue()
                .stream()
                .map(professionMapper::toResponse)
                .toList();
    }
}