
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

/**
 * Gère les opérations de consultation et de gestion des métiers du référentiel ECA.
 * Assure également la traçabilité des créations, modifications et désactivations
 * dans le journal d'audit.
 */
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

    /**
     * Récupère tous les métiers actifs et les convertit en DTO de réponse.
     *
     * @return la liste des métiers actifs
     */
    public List<ProfessionResponse> getAllActiveProfessions() {
        return professionRepository.findByActiveTrue()
                .stream()
                .map(professionMapper::toResponse)
                .toList();
    }

    /**
     * Récupère tous les métiers, qu'ils soient actifs ou inactifs.
     *
     * @return la liste de tous les métiers
     */
    public List<ProfessionResponse> getAllProfessions() {
        return professionRepository.findAll()
                .stream()
                .map(professionMapper::toResponse)
                .toList();
    }

    public List<ProfessionResponse> getAllInactiveProfessions() {
        return professionRepository.findByActiveFalse()
                .stream()
                .map(professionMapper::toResponse)
                .toList();
    }

    /**
     * Recherche un métier à partir de son identifiant.
     *
     * @param id identifiant du métier
     * @return le métier correspondant
     * @throws ResponseStatusException si le métier n'existe pas
     */
    public ProfessionResponse getProfessionById(String id) {
        Profession profession = professionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: " + id
                ));

        return professionMapper.toResponse(profession);
    }

    /**
     * Crée un métier après vérification de l'existence de sa catégorie.
     * Génère son identifiant et enregistre les informations dans le journal d'audit.
     *
     * @param request données du métier à créer
     * @return le métier créé
     * @throws ResponseStatusException si la catégorie n'existe pas
     */
    public ProfessionResponse createProfession(ProfessionRequest request) {

        // Vérifie que la catégorie associée existe avant de créer le métier.
        professionCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession category not found with id: " + request.categoryId()
                ));

        // Génère un identifiant unique à partir de la séquence des métiers.
        String id = sequenceGenerator.generate(
                SequenceDefinitions.PROFESSION_SEQUENCE,
                SequenceDefinitions.PROFESSION_PREFIX,
                SequenceDefinitions.PROFESSION_INITIAL_VALUE
        );

        // Crée le métier avec le statut actif par défaut.
        Profession profession = new Profession(
                request.name(),
                request.description(),
                true,
                request.categoryId()
        );

        profession.setId(id);

        // Persiste le métier avant d'enregistrer les événements d'audit associés.
        Profession savedProfession = professionRepository.save(profession);

        // Utilise le même horodatage pour toutes les entrées d'audit de cette création.
        LocalDateTime changedAt = LocalDateTime.now();

        // Enregistre individuellement les valeurs initiales des champs du métier.
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

    /**
     * Met à jour les informations d'un métier existant.
     * Seuls les champs modifiés sont enregistrés dans le journal d'audit.
     *
     * @param id      identifiant du métier à modifier
     * @param request nouvelles données du métier
     * @return le métier mis à jour
     * @throws ResponseStatusException si le métier ou sa catégorie n'existe pas
     */
    public ProfessionResponse updateProfession(String id, ProfessionRequest request) {

        // Vérifie que le métier à modifier existe.
        Profession profession = professionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: " + id
                ));

        // Vérifie que la nouvelle catégorie existe avant d'appliquer les modifications.
        professionCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession category not found with id: " + request.categoryId()
                ));

        // Prépare la liste des modifications à enregistrer dans le journal d'audit.
        List<AuditLog> auditLogs = new ArrayList<>();
        LocalDateTime changedAt = LocalDateTime.now();

        // Vérifie et met à jour le nom si nécessaire.
        if (!Objects.equals(profession.getName(), request.name())) {
            auditLogs.add(new AuditLog(
                    null, "PROFESSION", id, "name",
                    profession.getName(), request.name(),
                    "ADMIN", changedAt, "UPDATE"
            ));

            profession.setName(request.name());
        }

        // Vérifie et met à jour la description si nécessaire.
        if (!Objects.equals(profession.getDescription(), request.description())) {
            auditLogs.add(new AuditLog(
                    null, "PROFESSION", id, "description",
                    profession.getDescription(), request.description(),
                    "ADMIN", changedAt, "UPDATE"
            ));

            profession.setDescription(request.description());
        }

        // Vérifie et met à jour la catégorie si nécessaire.
        if (!Objects.equals(profession.getCategoryId(), request.categoryId())) {
            auditLogs.add(new AuditLog(
                    null, "PROFESSION", id, "categoryId",
                    profession.getCategoryId(), request.categoryId(),
                    "ADMIN", changedAt, "UPDATE"
            ));

            profession.setCategoryId(request.categoryId());
        }

        // Sauvegarde le métier avec ses nouvelles valeurs.
        Profession updatedProfession = professionRepository.save(profession);

        // Enregistre uniquement les modifications réellement effectuées.
        if (!auditLogs.isEmpty()) {
            auditLogRepository.saveAll(auditLogs);
        }

        return professionMapper.toResponse(updatedProfession);
    }

    /**
     * Désactive un métier sans le supprimer de la base de données.
     * Enregistre la modification dans le journal d'audit.
     * Une opération sur un métier déjà inactif reste sans effet.
     *
     * @param id identifiant du métier à désactiver
     * @throws ResponseStatusException si le métier n'existe pas
     */
    public void deactivateProfession(String id) {

        // Vérifie que le métier à désactiver existe.
        Profession profession = professionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profession not found with id: " + id
                ));

        // Ne fait rien si le métier est déjà inactif.
        if (!profession.isActive()) {
            return;
        }

        LocalDateTime changedAt = LocalDateTime.now();

        // Prépare l'entrée d'audit correspondant au changement de statut.
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

        // Désactive le métier sans supprimer son document MongoDB.
        profession.setActive(false);

        professionRepository.save(profession);

        // Enregistre la désactivation dans le journal d'audit.
        auditLogRepository.save(auditLog);
    }
}
