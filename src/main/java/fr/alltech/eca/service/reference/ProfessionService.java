
package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.profession.ProfessionRequest;
import fr.alltech.eca.dto.reference.profession.ProfessionResponse;
import fr.alltech.eca.mapper.reference.ProfessionMapper;
import fr.alltech.eca.model.audit.AuditLog;
import fr.alltech.eca.model.reference.Profession;
import fr.alltech.eca.repository.audit.AuditLogRepository;
import fr.alltech.eca.repository.reference.ProfessionCategoryRepository;
import fr.alltech.eca.repository.reference.ProfessionRepository;
import fr.alltech.eca.service.common.SequenceDefinitions;
import fr.alltech.eca.service.common.SequenceGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProfessionService {

    private final ProfessionRepository professionRepository;
    private final ProfessionCategoryRepository professionCategoryRepository;
    private final AuditLogRepository auditLogRepository;
    private final ProfessionMapper professionMapper;
    private final SequenceGenerator sequenceGenerator;

    public ProfessionService(
            ProfessionRepository professionRepository,
            ProfessionCategoryRepository professionCategoryRepository,
            AuditLogRepository auditLogRepository,
            ProfessionMapper professionMapper,
            SequenceGenerator sequenceGenerator
    ) {
        this.professionRepository = professionRepository;
        this.professionCategoryRepository = professionCategoryRepository;
        this.auditLogRepository = auditLogRepository;
        this.professionMapper = professionMapper;
        this.sequenceGenerator = sequenceGenerator;
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

    public ProfessionResponse createProfession(ProfessionRequest request) {

        professionCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession category not found with id: " + request.categoryId()
                ));

        String id = sequenceGenerator.generate(
                SequenceDefinitions.PROFESSION_SEQUENCE,
                SequenceDefinitions.PROFESSION_PREFIX,
                SequenceDefinitions.PROFESSION_INITIAL_VALUE
        );

        Profession profession = new Profession(
                request.name(),
                request.description(),
                true,
                request.categoryId()
        );

        profession.setId(id);

        Profession savedProfession = professionRepository.save(profession);

        LocalDateTime changedAt = LocalDateTime.now();

        auditLogRepository.saveAll(List.of(
                new AuditLog(
                        null, "PROFESSION", id, "name",
                        null, request.name(), "ADMIN", changedAt, "CREATE"
                ),
                new AuditLog(
                        null, "PROFESSION", id, "description",
                        null, request.description(), "ADMIN", changedAt, "CREATE"
                ),
                new AuditLog(
                        null, "PROFESSION", id, "categoryId",
                        null, request.categoryId(), "ADMIN", changedAt, "CREATE"
                ),
                new AuditLog(
                        null, "PROFESSION", id, "active",
                        null, "true", "ADMIN", changedAt, "CREATE"
                )
        ));

        return professionMapper.toResponse(savedProfession);
    }

    public ProfessionResponse updateProfession(String id, ProfessionRequest request) {

        Profession profession = professionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: " + id
                ));

        professionCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession category not found with id: " + request.categoryId()
                ));

        List<AuditLog> auditLogs = new ArrayList<>();
        LocalDateTime changedAt = LocalDateTime.now();

        // Check and update the name
        if (!Objects.equals(profession.getName(), request.name())) {
            auditLogs.add(new AuditLog(
                    null, "PROFESSION", id, "name",
                    profession.getName(), request.name(),
                    "ADMIN", changedAt, "UPDATE"
            ));

            profession.setName(request.name());
        }

        // Check and update the description
        if (!Objects.equals(profession.getDescription(), request.description())) {
            auditLogs.add(new AuditLog(
                    null, "PROFESSION", id, "description",
                    profession.getDescription(), request.description(),
                    "ADMIN", changedAt, "UPDATE"
            ));

            profession.setDescription(request.description());
        }

        // Check and update the category
        if (!Objects.equals(profession.getCategoryId(), request.categoryId())) {
            auditLogs.add(new AuditLog(
                    null, "PROFESSION", id, "categoryId",
                    profession.getCategoryId(), request.categoryId(),
                    "ADMIN", changedAt, "UPDATE"
            ));

            profession.setCategoryId(request.categoryId());
        }

        Profession updatedProfession = professionRepository.save(profession);

        if (!auditLogs.isEmpty()) {
            auditLogRepository.saveAll(auditLogs);
        }

        return professionMapper.toResponse(updatedProfession);
    }

    public void deactivateProfession(String id) {

        Profession profession = professionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: " + id
                ));

        // Do nothing if the profession is already inactive.
        if (!profession.isActive()) {
            return;
        }

        LocalDateTime changedAt = LocalDateTime.now();

        AuditLog auditLog = new AuditLog(
                null,
                "PROFESSION",
                id,
                "active",
                "true",
                "false",
                "ADMIN",
                changedAt,
                "DELETE"
        );

        profession.setActive(false);

        professionRepository.save(profession);
        auditLogRepository.save(auditLog);
    }
}
