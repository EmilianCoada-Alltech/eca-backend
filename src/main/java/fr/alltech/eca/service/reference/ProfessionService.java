package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.ProfessionResponse;
import fr.alltech.eca.mapper.reference.ProfessionMapper;
import fr.alltech.eca.model.reference.Profession;
import fr.alltech.eca.repository.reference.ProfessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public List<ProfessionResponse> getAllActiveProfessions() {
        return professionRepository.findByActiveTrue()
                .stream()
                .map(professionMapper::toResponse)
                .toList();
    }

    public List<ProfessionResponse> getAllProfessions() {
        return professionRepository.findAll()
                .stream()
                .map(professionMapper::toResponse)
                .toList();
    }

    public ProfessionResponse getProfessionById(String id) {
        Profession profession = professionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: " + id
                ));

        return professionMapper.toResponse(profession);
    }
}