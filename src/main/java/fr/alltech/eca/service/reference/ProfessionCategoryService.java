package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.profession_category.ProfessionCategoryRequest;
import fr.alltech.eca.dto.reference.profession_category.ProfessionCategoryResponse;
import fr.alltech.eca.mapper.reference.ProfessionCategoryMapper;
import fr.alltech.eca.model.audit.AuditLog;
import fr.alltech.eca.model.reference.ProfessionCategory;
import fr.alltech.eca.repository.audit.AuditLogRepository;
import fr.alltech.eca.repository.reference.ProfessionCategoryRepository;
import fr.alltech.eca.service.common.SequenceDefinitions;
import fr.alltech.eca.service.common.SequenceGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Gère la consultation des catégories de métiers du référentiel ECA.
 */
@Service
public class ProfessionCategoryService {

    private final ProfessionCategoryRepository professionCategoryRepository;
    private final ProfessionCategoryMapper professionCategoryMapper;
    private final SequenceGenerator sequenceGenerator;
    private final AuditLogRepository auditLogRepository;

    public ProfessionCategoryService(
            ProfessionCategoryRepository professionCategoryRepository,
            ProfessionCategoryMapper professionCategoryMapper,
            SequenceGenerator sequenceGenerator, AuditLogRepository auditLogRepository) {
        this.professionCategoryRepository = professionCategoryRepository;
        this.professionCategoryMapper = professionCategoryMapper;
        this.sequenceGenerator = sequenceGenerator;
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Récupère toutes les catégories de métiers et les convertit en DTO de réponse.
     *
     * @return la liste des catégories de métiers
     */
    public List<ProfessionCategoryResponse> getAllProfessionCategories() {
        return professionCategoryRepository.findAll()
                .stream()
                .map(professionCategoryMapper::toResponse)
                .toList();
    }

    public List<ProfessionCategoryResponse> getAllActiveProfessionCategories() {
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

    public ProfessionCategoryResponse getProfessionCategoryById(String id) {
        ProfessionCategory professionCategory = professionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Profession Category not found with id: " + id
                ));

        return professionCategoryMapper.toResponse(professionCategory);
    }

    public ProfessionCategoryResponse createProfessionCategory(ProfessionCategoryRequest request) {

        String id = sequenceGenerator.generate(
                SequenceDefinitions.PROFESSION_CATEGORY_SEQUENCE,
                SequenceDefinitions.PROFESSION_CATEGORY_PREFIX,
                SequenceDefinitions.PROFESSION_CATEGORY_INITIAL_VALUE
        );

        ProfessionCategory professionCategory = new ProfessionCategory(
                request.name(),
                request.description(),
                true
        );

        professionCategory.setId(id);

        ProfessionCategory savedCategory = professionCategoryRepository.save(professionCategory);

        LocalDateTime changedAt = LocalDateTime.now();

        auditLogRepository.saveAll(List.of(
                new AuditLog(
                        null, "PROFESSION-CATEGORY", id, "name",
                        null, request.name(), "Admin", changedAt, "CREATE"
                ),
                new AuditLog(
                        null, "PROFESSION-CATEGORY", id, "description",
                        null, request.description(), "ADMIN", changedAt, "CREATE"
                ),
                new AuditLog(
                        null, "PROFESSION-CATEGORY", id, "active",
                        null, "true", "ADMIN", changedAt, "CREATE"
                )
        ));

        return professionCategoryMapper.toResponse(savedCategory);
    }

    public ProfessionCategoryResponse updateProfessionCategory(String id, ProfessionCategoryRequest request) {

        ProfessionCategory category = professionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession Category not found with id: " + id
                ));

        List<AuditLog> auditLogs = new ArrayList<>();
        LocalDateTime changedAt = LocalDateTime.now();

        if (request.name() != null
                && !Objects.equals(category.getName(), request.name())) {
            auditLogs.add(new AuditLog(
                    null, "PROFESSION-CATEGORY", id, "name",
                    category.getName(), request.name(),
                    "ADMIN", changedAt, "UPDATE"
            ));

            category.setName(request.name());
        }

        if (request.description() != null
                && !Objects.equals(category.getDescription(), request.description())) {
            auditLogs.add(new AuditLog(
                    null, "PROFESSION-CATEGORY", id, "description",
                    category.getDescription(), request.description(),
                    "ADMIN", changedAt, "UPDATE"
            ));

            category.setDescription(request.description());
        }

        ProfessionCategory updatedCategory = professionCategoryRepository.save(category);

        if (!auditLogs.isEmpty()) {
            auditLogRepository.saveAll(auditLogs);
        }

        return professionCategoryMapper.toResponse(updatedCategory);
    }

    public ProfessionCategoryResponse toggleProfessionCategoryStatus(String id) {

        ProfessionCategory category = professionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession category not found with id: " + id
                ));

        boolean previousStatus = category.isActive();
        boolean newStatus = !previousStatus;

        LocalDateTime changedAt = LocalDateTime.now();

        AuditLog auditLog = new AuditLog(
                null,
                "PROFESSION-CATEGORY",
                id,
                "active",
                String.valueOf(previousStatus),
                String.valueOf(newStatus),
                "ADMIN",
                changedAt,
                "UPDATE"
        );

        category.setActive(newStatus);

        ProfessionCategory savedCategory = professionCategoryRepository.save(category);

        auditLogRepository.save(new AuditLog());

        return professionCategoryMapper.toResponse(savedCategory);
    }
}