package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.profession.ProfessionRequest;
import fr.alltech.eca.dto.reference.profession.ProfessionResponse;
import fr.alltech.eca.mapper.reference.ProfessionMapper;
import fr.alltech.eca.model.audit.AuditLog;
import fr.alltech.eca.model.reference.Profession;
import fr.alltech.eca.model.reference.ProfessionCategory;
import fr.alltech.eca.repository.association.ProfessionCompetenceRepository;
import fr.alltech.eca.repository.audit.AuditLogRepository;
import fr.alltech.eca.repository.collaborator.CollaboratorRepository;
import fr.alltech.eca.repository.reference.ProfessionCategoryRepository;
import fr.alltech.eca.repository.reference.ProfessionRepository;
import fr.alltech.eca.service.common.SequenceDefinitions;
import fr.alltech.eca.service.common.SequenceGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires complets de ProfessionService.
 * <p>
 * Les tests couvrent la consultation, la création, la mise à jour partielle,
 * le changement de statut, la suppression physique, les contrôles de dépendances
 * et la journalisation des opérations.
 */
@ExtendWith(MockitoExtension.class)
class ProfessionServiceTest {

    @Mock
    private ProfessionRepository professionRepository;

    @Mock
    private ProfessionCategoryRepository professionCategoryRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Mock
    private CollaboratorRepository collaboratorRepository;

    @Mock
    private ProfessionCompetenceRepository professionCompetenceRepository;

    private ProfessionService professionService;

    @BeforeEach
    void setUp() {
        professionService = new ProfessionService(
                professionRepository,
                professionCategoryRepository,
                auditLogRepository,
                new ProfessionMapper(),
                sequenceGenerator,
                collaboratorRepository,
                professionCompetenceRepository
        );
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Profession profession(
            String id,
            String name,
            String description,
            boolean active,
            String categoryId
    ) {
        Profession profession = new Profession(name, description, active, categoryId);
        profession.setId(id);
        return profession;
    }

    private ProfessionCategory category(String id) {
        ProfessionCategory category = new ProfessionCategory(
                "Development",
                "Development professions",
                true
        );
        category.setId(id);
        return category;
    }

    private void mockSaveProfession() {
        when(professionRepository.save(any(Profession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private List<AuditLog> captureAuditLogs() {
        ArgumentCaptor<List<AuditLog>> captor = ArgumentCaptor.forClass(List.class);
        verify(auditLogRepository).saveAll(captor.capture());
        return captor.getValue();
    }

    private void assertStatus(ResponseStatusException exception, HttpStatus expected) {
        assertThat(exception.getStatusCode()).isEqualTo(expected);
    }

    // -------------------------------------------------------------------------
    // Read operations
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnActiveProfessionsAsResponses() {
        Profession active = profession(
                "PROF-001", "Backend developer", "Builds backend services.", true, "CAT-001"
        );
        when(professionRepository.findByActiveTrue()).thenReturn(List.of(active));

        List<ProfessionResponse> result = professionService.getAllActiveProfessions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0))
                .extracting(
                        ProfessionResponse::id,
                        ProfessionResponse::name,
                        ProfessionResponse::description,
                        ProfessionResponse::categoryId,
                        ProfessionResponse::active
                )
                .containsExactly(
                        "PROF-001",
                        "Backend developer",
                        "Builds backend services.",
                        "CAT-001",
                        true
                );
        verify(professionRepository).findByActiveTrue();
        verifyNoMoreInteractions(professionRepository);
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveProfessionsExist() {
        when(professionRepository.findByActiveTrue()).thenReturn(List.of());

        assertThat(professionService.getAllActiveProfessions()).isEmpty();

        verify(professionRepository).findByActiveTrue();
    }

    @Test
    void shouldReturnInactiveProfessionsAsResponses() {
        Profession inactive = profession(
                "PROF-002", "Legacy developer", "No longer used.", false, "CAT-001"
        );
        when(professionRepository.findByActiveFalse()).thenReturn(List.of(inactive));

        List<ProfessionResponse> result = professionService.getAllInactiveProfessions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("PROF-002");
        assertThat(result.get(0).active()).isFalse();
        verify(professionRepository).findByActiveFalse();
    }

    @Test
    void shouldReturnEmptyListWhenNoInactiveProfessionsExist() {
        when(professionRepository.findByActiveFalse()).thenReturn(List.of());

        assertThat(professionService.getAllInactiveProfessions()).isEmpty();

        verify(professionRepository).findByActiveFalse();
    }

    @Test
    void shouldReturnAllProfessionsIncludingActiveAndInactive() {
        Profession active = profession(
                "PROF-001", "Backend developer", "Builds APIs.", true, "CAT-001"
        );
        Profession inactive = profession(
                "PROF-002", "Legacy developer", "Deprecated.", false, "CAT-001"
        );
        when(professionRepository.findAll()).thenReturn(List.of(active, inactive));

        List<ProfessionResponse> result = professionService.getAllProfessions();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ProfessionResponse::id)
                .containsExactly("PROF-001", "PROF-002");
        assertThat(result)
                .extracting(ProfessionResponse::active)
                .containsExactly(true, false);
        verify(professionRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoProfessionsExist() {
        when(professionRepository.findAll()).thenReturn(List.of());

        assertThat(professionService.getAllProfessions()).isEmpty();

        verify(professionRepository).findAll();
    }

    @Test
    void shouldReturnProfessionByIdEvenWhenInactive() {
        Profession inactive = profession(
                "PROF-003", "Legacy developer", "Inactive profession.", false, "CAT-004"
        );
        when(professionRepository.findById("PROF-003")).thenReturn(Optional.of(inactive));

        ProfessionResponse result = professionService.getProfessionById("PROF-003");

        assertThat(result.id()).isEqualTo("PROF-003");
        assertThat(result.name()).isEqualTo("Legacy developer");
        assertThat(result.categoryId()).isEqualTo("CAT-004");
        assertThat(result.active()).isFalse();
        verify(professionRepository).findById("PROF-003");
    }

    @Test
    void shouldThrowNotFoundWhenGettingUnknownProfession() {
        when(professionRepository.findById("PROF-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionService.getProfessionById("PROF-404"))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertStatus(exception, HttpStatus.NOT_FOUND));

        verify(professionRepository).findById("PROF-404");
    }

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    @Test
    void shouldCreateProfessionWithGeneratedIdAndFourAuditEntries() {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "Builds backend services.",
                "CAT-001"
        );
        when(professionCategoryRepository.findById("CAT-001"))
                .thenReturn(Optional.of(category("CAT-001")));
        when(sequenceGenerator.generate(
                SequenceDefinitions.PROFESSION_SEQUENCE,
                SequenceDefinitions.PROFESSION_PREFIX,
                SequenceDefinitions.PROFESSION_INITIAL_VALUE
        )).thenReturn("PROF-063");
        mockSaveProfession();

        ProfessionResponse result = professionService.createProfession(request);

        assertThat(result.id()).isEqualTo("PROF-063");
        assertThat(result.name()).isEqualTo("Backend developer");
        assertThat(result.description()).isEqualTo("Builds backend services.");
        assertThat(result.categoryId()).isEqualTo("CAT-001");
        assertThat(result.active()).isTrue();

        verify(professionCategoryRepository).findById("CAT-001");
        verify(sequenceGenerator).generate(
                SequenceDefinitions.PROFESSION_SEQUENCE,
                SequenceDefinitions.PROFESSION_PREFIX,
                SequenceDefinitions.PROFESSION_INITIAL_VALUE
        );
        verify(professionRepository).save(argThat(saved ->
                "PROF-063".equals(saved.getId())
                        && "Backend developer".equals(saved.getName())
                        && saved.isActive()
        ));

        List<AuditLog> logs = captureAuditLogs();
        assertThat(logs).hasSize(4);
        assertThat(logs).extracting(AuditLog::getAction).containsOnly("CREATE");
        assertThat(logs).extracting(AuditLog::getEntityId).containsOnly("PROF-063");
        assertThat(logs).extracting(AuditLog::getChangedBy).containsOnly("ADMIN");
        assertThat(logs).extracting(AuditLog::getFieldName)
                .containsExactlyInAnyOrder("name", "description", "categoryId", "active");
        assertThat(logs).extracting(AuditLog::getNewValue)
                .containsExactlyInAnyOrder(
                        "Backend developer",
                        "Builds backend services.",
                        "CAT-001",
                        "true"
                );
    }

    @Test
    void shouldThrowNotFoundAndStopCreationWhenCategoryDoesNotExist() {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer", "Builds APIs.", "CAT-404"
        );
        when(professionCategoryRepository.findById("CAT-404"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionService.createProfession(request))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertStatus(exception, HttpStatus.NOT_FOUND));

        verify(professionCategoryRepository).findById("CAT-404");
        verifyNoInteractions(sequenceGenerator, professionRepository, auditLogRepository);
    }

    // -------------------------------------------------------------------------
    // Update — partial update semantics
    // -------------------------------------------------------------------------

    @Test
    void shouldUpdateAllChangedFieldsAndAuditEachChange() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Old description.", true, "CAT-001"
        );
        ProfessionRequest request = new ProfessionRequest(
                "Senior backend developer", "New description.", "CAT-002"
        );
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        when(professionCategoryRepository.findById("CAT-002"))
                .thenReturn(Optional.of(category("CAT-002")));
        mockSaveProfession();

        ProfessionResponse result = professionService.updateProfession("PROF-001", request);

        assertThat(result.name()).isEqualTo("Senior backend developer");
        assertThat(result.description()).isEqualTo("New description.");
        assertThat(result.categoryId()).isEqualTo("CAT-002");
        assertThat(result.active()).isTrue();

        List<AuditLog> logs = captureAuditLogs();
        assertThat(logs).hasSize(3);
        assertThat(logs).extracting(AuditLog::getAction).containsOnly("UPDATE");
        assertThat(logs).extracting(AuditLog::getFieldName)
                .containsExactlyInAnyOrder("name", "description", "categoryId");
        assertThat(logs).extracting(AuditLog::getEntityId).containsOnly("PROF-001");
        verify(professionRepository).save(existing);
    }

    @Test
    void shouldUpdateOnlyNameAndPreserveDescriptionAndCategoryWhenTheyAreNull() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Keep this description.", true, "CAT-001"
        );
        ProfessionRequest request = new ProfessionRequest("Senior developer", null, null);
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        mockSaveProfession();

        ProfessionResponse result = professionService.updateProfession("PROF-001", request);

        assertThat(result.name()).isEqualTo("Senior developer");
        assertThat(result.description()).isEqualTo("Keep this description.");
        assertThat(result.categoryId()).isEqualTo("CAT-001");

        List<AuditLog> logs = captureAuditLogs();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getFieldName()).isEqualTo("name");
        verifyNoInteractions(professionCategoryRepository);
    }

    @Test
    void shouldUpdateOnlyDescriptionAndPreserveOtherFieldsWhenTheyAreNull() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Old description.", true, "CAT-001"
        );
        ProfessionRequest request = new ProfessionRequest(null, "Updated description.", null);
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        mockSaveProfession();

        ProfessionResponse result = professionService.updateProfession("PROF-001", request);

        assertThat(result.name()).isEqualTo("Backend developer");
        assertThat(result.description()).isEqualTo("Updated description.");
        assertThat(result.categoryId()).isEqualTo("CAT-001");

        List<AuditLog> logs = captureAuditLogs();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getFieldName()).isEqualTo("description");
        verifyNoInteractions(professionCategoryRepository);
    }

    @Test
    void shouldUpdateOnlyCategoryAndPreserveOtherFieldsWhenTheyAreNull() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Existing description.", true, "CAT-001"
        );
        ProfessionRequest request = new ProfessionRequest(null, null, "CAT-002");
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        when(professionCategoryRepository.findById("CAT-002"))
                .thenReturn(Optional.of(category("CAT-002")));
        mockSaveProfession();

        ProfessionResponse result = professionService.updateProfession("PROF-001", request);

        assertThat(result.name()).isEqualTo("Backend developer");
        assertThat(result.description()).isEqualTo("Existing description.");
        assertThat(result.categoryId()).isEqualTo("CAT-002");

        List<AuditLog> logs = captureAuditLogs();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getFieldName()).isEqualTo("categoryId");
    }

    @Test
    void shouldNotAuditOrLookupCategoryWhenSubmittedValuesAreUnchanged() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Same description.", true, "CAT-001"
        );
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer", "Same description.", "CAT-001"
        );
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        mockSaveProfession();

        ProfessionResponse result = professionService.updateProfession("PROF-001", request);

        assertThat(result.name()).isEqualTo("Backend developer");
        assertThat(result.description()).isEqualTo("Same description.");
        assertThat(result.categoryId()).isEqualTo("CAT-001");
        verify(professionRepository).save(existing);
        verifyNoInteractions(professionCategoryRepository);
        verify(auditLogRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldSaveWithoutAuditWhenAllUpdateFieldsAreNull() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Existing description.", true, "CAT-001"
        );
        ProfessionRequest request = new ProfessionRequest(null, null, null);
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        mockSaveProfession();

        ProfessionResponse result = professionService.updateProfession("PROF-001", request);

        assertThat(result.name()).isEqualTo("Backend developer");
        assertThat(result.description()).isEqualTo("Existing description.");
        assertThat(result.categoryId()).isEqualTo("CAT-001");
        verify(professionRepository).save(existing);
        verifyNoInteractions(professionCategoryRepository);
        verify(auditLogRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingUnknownProfession() {
        when(professionRepository.findById("PROF-404")).thenReturn(Optional.empty());
        ProfessionRequest request = new ProfessionRequest("New name", null, null);

        assertThatThrownBy(() -> professionService.updateProfession("PROF-404", request))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertStatus(exception, HttpStatus.NOT_FOUND));

        verifyNoInteractions(
                professionCategoryRepository,
                auditLogRepository
        );
        verify(professionRepository, never()).save(any(Profession.class));
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingToUnknownCategoryWithoutSavingOrAuditing() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Description.", true, "CAT-001"
        );
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        when(professionCategoryRepository.findById("CAT-404")).thenReturn(Optional.empty());
        ProfessionRequest request = new ProfessionRequest(null, null, "CAT-404");

        assertThatThrownBy(() -> professionService.updateProfession("PROF-001", request))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertStatus(exception, HttpStatus.NOT_FOUND));

        verify(professionRepository, never()).save(any(Profession.class));
        verify(auditLogRepository, never()).saveAll(anyList());
    }

    // -------------------------------------------------------------------------
    // Status toggle
    // -------------------------------------------------------------------------

    @Test
    void shouldActivateInactiveProfessionAndRecordStatusChange() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Description.", false, "CAT-001"
        );
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        mockSaveProfession();

        ProfessionResponse result = professionService.toggleProfessionStatus("PROF-001");

        assertThat(result.active()).isTrue();
        assertThat(existing.isActive()).isTrue();
        verify(professionRepository).save(existing);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLog log = captor.getValue();
        assertThat(log.getEntityId()).isEqualTo("PROF-001");
        assertThat(log.getFieldName()).isEqualTo("active");
        assertThat(log.getOldValue()).isEqualTo("false");
        assertThat(log.getNewValue()).isEqualTo("true");
        assertThat(log.getAction()).isEqualTo("UPDATE");
        assertThat(log.getChangedBy()).isEqualTo("ADMIN");
    }

    @Test
    void shouldDeactivateActiveProfessionAndRecordStatusChange() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Description.", true, "CAT-001"
        );
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        mockSaveProfession();

        ProfessionResponse result = professionService.toggleProfessionStatus("PROF-001");

        assertThat(result.active()).isFalse();
        assertThat(existing.isActive()).isFalse();

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertThat(captor.getValue().getOldValue()).isEqualTo("true");
        assertThat(captor.getValue().getNewValue()).isEqualTo("false");
        assertThat(captor.getValue().getAction()).isEqualTo("UPDATE");
    }

    @Test
    void shouldThrowNotFoundWhenTogglingUnknownProfession() {
        when(professionRepository.findById("PROF-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionService.toggleProfessionStatus("PROF-404"))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertStatus(exception, HttpStatus.NOT_FOUND));

        verify(professionRepository, never()).save(any(Profession.class));
        verifyNoInteractions(auditLogRepository);
    }

    // -------------------------------------------------------------------------
    // Physical delete and dependency protection
    // -------------------------------------------------------------------------

    @Test
    void shouldPhysicallyDeleteUnreferencedProfessionAndRecordAudit() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Description.", true, "CAT-001"
        );
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        when(collaboratorRepository.existsByProfessionId("PROF-001")).thenReturn(false);
        when(professionCompetenceRepository.existsByProfessionId("PROF-001")).thenReturn(false);

        professionService.deleteProfession("PROF-001");

        verify(collaboratorRepository).existsByProfessionId("PROF-001");
        verify(professionCompetenceRepository).existsByProfessionId("PROF-001");
        verify(professionRepository).delete(existing);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLog log = captor.getValue();
        assertThat(log.getEntityId()).isEqualTo("PROF-001");
        assertThat(log.getFieldName()).isEqualTo("profession");
        assertThat(log.getOldValue()).isEqualTo("Backend developer");
        assertThat(log.getNewValue()).isNull();
        assertThat(log.getAction()).isEqualTo("DELETE");
        assertThat(log.getChangedBy()).isEqualTo("ADMIN");
    }

    @Test
    void shouldThrowNotFoundWhenDeletingUnknownProfession() {
        when(professionRepository.findById("PROF-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionService.deleteProfession("PROF-404"))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertStatus(exception, HttpStatus.NOT_FOUND));

        verifyNoInteractions(collaboratorRepository, professionCompetenceRepository, auditLogRepository);
        verify(professionRepository, never()).delete(any(Profession.class));
    }

    @Test
    void shouldPreventDeletionWhenProfessionIsAssignedToCollaborators() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Description.", true, "CAT-001"
        );
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        when(collaboratorRepository.existsByProfessionId("PROF-001")).thenReturn(true);

        assertThatThrownBy(() -> professionService.deleteProfession("PROF-001"))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertStatus(exception, HttpStatus.CONFLICT));

        verify(collaboratorRepository).existsByProfessionId("PROF-001");
        verify(professionCompetenceRepository, never()).existsByProfessionId("PROF-001");
        verify(professionRepository, never()).delete(any(Profession.class));
        verifyNoInteractions(auditLogRepository);
    }

    @Test
    void shouldPreventDeletionWhenProfessionHasCompetenceAssociations() {
        Profession existing = profession(
                "PROF-001", "Backend developer", "Description.", true, "CAT-001"
        );
        when(professionRepository.findById("PROF-001")).thenReturn(Optional.of(existing));
        when(collaboratorRepository.existsByProfessionId("PROF-001")).thenReturn(false);
        when(professionCompetenceRepository.existsByProfessionId("PROF-001")).thenReturn(true);

        assertThatThrownBy(() -> professionService.deleteProfession("PROF-001"))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertStatus(exception, HttpStatus.CONFLICT));

        verify(collaboratorRepository).existsByProfessionId("PROF-001");
        verify(professionCompetenceRepository).existsByProfessionId("PROF-001");
        verify(professionRepository, never()).delete(any(Profession.class));
        verifyNoInteractions(auditLogRepository);
    }
}
