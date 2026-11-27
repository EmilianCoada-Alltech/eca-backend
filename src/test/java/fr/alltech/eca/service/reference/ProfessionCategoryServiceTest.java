package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.profession_category.ProfessionCategoryRequest;
import fr.alltech.eca.dto.reference.profession_category.ProfessionCategoryResponse;
import fr.alltech.eca.mapper.reference.ProfessionCategoryMapper;
import fr.alltech.eca.model.audit.AuditLog;
import fr.alltech.eca.model.reference.ProfessionCategory;
import fr.alltech.eca.repository.audit.AuditLogRepository;
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

@ExtendWith(MockitoExtension.class)
class ProfessionCategoryServiceTest {

    @Mock
    private ProfessionCategoryRepository professionCategoryRepository;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private ProfessionRepository professionRepository;

    private ProfessionCategoryService professionCategoryService;

    @BeforeEach
    void setUp() {
        professionCategoryService = new ProfessionCategoryService(
                professionCategoryRepository,
                new ProfessionCategoryMapper(),
                sequenceGenerator,
                auditLogRepository,
                professionRepository
        );
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ProfessionCategory category(
            String id,
            String name,
            String description,
            boolean active
    ) {
        ProfessionCategory category = new ProfessionCategory(name, description, active);
        category.setId(id);
        return category;
    }

    private ProfessionCategoryRequest request(String name, String description) {
        return new ProfessionCategoryRequest(name, description);
    }

    private void mockSaveCategory() {
        when(professionCategoryRepository.save(any(ProfessionCategory.class)))
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
    void shouldReturnAllProfessionCategories() {
        ProfessionCategory first = category(
                "PCAT-001", "Development", "Development roles", true
        );
        ProfessionCategory second = category(
                "PCAT-002", "Administration", "Administrative roles", false
        );

        when(professionCategoryRepository.findAll())
                .thenReturn(List.of(first, second));

        List<ProfessionCategoryResponse> result =
                professionCategoryService.getAllProfessionCategories();

        assertThat(result)
                .extracting(ProfessionCategoryResponse::id)
                .containsExactly("PCAT-001", "PCAT-002");

        assertThat(result)
                .extracting(ProfessionCategoryResponse::name)
                .containsExactly("Development", "Administration");

        verify(professionCategoryRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoCategories() {
        when(professionCategoryRepository.findAll()).thenReturn(List.of());

        assertThat(professionCategoryService.getAllProfessionCategories()).isEmpty();
    }

    @Test
    void shouldReturnOnlyActiveCategories() {
        ProfessionCategory active = category(
                "PCAT-001", "Development", "Development roles", true
        );

        when(professionCategoryRepository.findByActiveTrue())
                .thenReturn(List.of(active));

        List<ProfessionCategoryResponse> result =
                professionCategoryService.getAllActiveProfessionCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("PCAT-001");
        assertThat(result.get(0).active()).isTrue();

        verify(professionCategoryRepository).findByActiveTrue();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoActiveCategories() {
        when(professionCategoryRepository.findByActiveTrue()).thenReturn(List.of());

        assertThat(professionCategoryService.getAllActiveProfessionCategories()).isEmpty();
    }

    @Test
    void shouldReturnOnlyInactiveCategories() {
        ProfessionCategory inactive = category(
                "PCAT-002", "Legacy", "Legacy roles", false
        );

        when(professionCategoryRepository.findByActiveFalse())
                .thenReturn(List.of(inactive));

        List<ProfessionCategoryResponse> result =
                professionCategoryService.getAllInactiveProfessionCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("PCAT-002");
        assertThat(result.get(0).active()).isFalse();

        verify(professionCategoryRepository).findByActiveFalse();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoInactiveCategories() {
        when(professionCategoryRepository.findByActiveFalse()).thenReturn(List.of());

        assertThat(professionCategoryService.getAllInactiveProfessionCategories()).isEmpty();
    }

    @Test
    void shouldReturnCategoryById() {
        ProfessionCategory existing = category(
                "PCAT-001", "Development", "Development roles", true
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(existing));

        ProfessionCategoryResponse result =
                professionCategoryService.getProfessionCategoryById("PCAT-001");

        assertThat(result.id()).isEqualTo("PCAT-001");
        assertThat(result.name()).isEqualTo("Development");
        assertThat(result.description()).isEqualTo("Development roles");
        assertThat(result.active()).isTrue();
    }

    @Test
    void shouldReturnInactiveCategoryById() {
        ProfessionCategory inactive = category(
                "PCAT-002", "Legacy", "Legacy roles", false
        );

        when(professionCategoryRepository.findById("PCAT-002"))
                .thenReturn(Optional.of(inactive));

        ProfessionCategoryResponse result =
                professionCategoryService.getProfessionCategoryById("PCAT-002");

        assertThat(result.active()).isFalse();
    }

    @Test
    void shouldThrowNotFoundWhenCategoryDoesNotExist() {
        when(professionCategoryRepository.findById("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                professionCategoryService.getProfessionCategoryById("UNKNOWN"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertStatus(
                                (ResponseStatusException) exception,
                                HttpStatus.NOT_FOUND
                        ));
    }

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    @Test
    void shouldCreateCategoryWithGeneratedIdAndActiveStatus() {
        ProfessionCategoryRequest request =
                request("Development", "Development roles");

        when(sequenceGenerator.generate(
                SequenceDefinitions.PROFESSION_CATEGORY_SEQUENCE,
                SequenceDefinitions.PROFESSION_CATEGORY_PREFIX,
                SequenceDefinitions.PROFESSION_CATEGORY_INITIAL_VALUE
        )).thenReturn("PCAT-015");

        mockSaveCategory();

        ProfessionCategoryResponse result =
                professionCategoryService.createProfessionCategory(request);

        assertThat(result.id()).isEqualTo("PCAT-015");
        assertThat(result.name()).isEqualTo("Development");
        assertThat(result.description()).isEqualTo("Development roles");
        assertThat(result.active()).isTrue();

        verify(sequenceGenerator).generate(
                SequenceDefinitions.PROFESSION_CATEGORY_SEQUENCE,
                SequenceDefinitions.PROFESSION_CATEGORY_PREFIX,
                SequenceDefinitions.PROFESSION_CATEGORY_INITIAL_VALUE
        );

        verify(professionCategoryRepository).save(argThat(saved ->
                "PCAT-015".equals(saved.getId())
                        && "Development".equals(saved.getName())
                        && "Development roles".equals(saved.getDescription())
                        && saved.isActive()
        ));
    }

    @Test
    void shouldWriteThreeAuditEntriesWhenCreatingCategory() {
        ProfessionCategoryRequest request =
                request("Development", "Development roles");

        // Use the exact arguments to avoid primitive matcher unboxing issues.
        when(sequenceGenerator.generate(
                SequenceDefinitions.PROFESSION_CATEGORY_SEQUENCE,
                SequenceDefinitions.PROFESSION_CATEGORY_PREFIX,
                SequenceDefinitions.PROFESSION_CATEGORY_INITIAL_VALUE
        )).thenReturn("PCAT-015");

        mockSaveCategory();

        professionCategoryService.createProfessionCategory(request);

        List<AuditLog> logs = captureAuditLogs();

        assertThat(logs).hasSize(3);
        assertThat(logs)
                .extracting(AuditLog::getAction)
                .containsOnly("CREATE");

        assertThat(logs)
                .extracting(AuditLog::getEntityId)
                .containsOnly("PCAT-015");

        assertThat(logs)
                .extracting(AuditLog::getFieldName)
                .containsExactlyInAnyOrder("name", "description", "active");

        assertThat(logs)
                .extracting(AuditLog::getOldValue)
                .containsOnlyNulls();

        assertThat(logs)
                .extracting(AuditLog::getNewValue)
                .containsExactlyInAnyOrder(
                        "Development",
                        "Development roles",
                        "true"
                );
    }

    // -------------------------------------------------------------------------
    // Update - partial update semantics
    // -------------------------------------------------------------------------

    @Test
    void shouldUpdateBothNameAndDescription() {
        ProfessionCategory existing = category(
                "PCAT-001", "Old name", "Old description", true
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(existing));

        mockSaveCategory();

        ProfessionCategoryResponse result =
                professionCategoryService.updateProfessionCategory(
                        "PCAT-001",
                        request("New name", "New description")
                );

        assertThat(result.name()).isEqualTo("New name");
        assertThat(result.description()).isEqualTo("New description");
        assertThat(result.active()).isTrue();

        List<AuditLog> logs = captureAuditLogs();

        assertThat(logs).hasSize(2);
        assertThat(logs)
                .extracting(AuditLog::getFieldName)
                .containsExactlyInAnyOrder("name", "description");

        assertThat(logs)
                .extracting(AuditLog::getAction)
                .containsOnly("UPDATE");
    }

    @Test
    void shouldUpdateOnlyNameAndPreserveDescription() {
        ProfessionCategory existing = category(
                "PCAT-001", "Old name", "Keep this description", true
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(existing));

        mockSaveCategory();

        ProfessionCategoryResponse result =
                professionCategoryService.updateProfessionCategory(
                        "PCAT-001",
                        request("New name", null)
                );

        assertThat(result.name()).isEqualTo("New name");
        assertThat(result.description()).isEqualTo("Keep this description");

        assertThat(captureAuditLogs())
                .singleElement()
                .satisfies(log -> {
                    assertThat(log.getFieldName()).isEqualTo("name");
                    assertThat(log.getOldValue()).isEqualTo("Old name");
                    assertThat(log.getNewValue()).isEqualTo("New name");
                });
    }

    @Test
    void shouldUpdateOnlyDescriptionAndPreserveName() {
        ProfessionCategory existing = category(
                "PCAT-001", "Keep this name", "Old description", true
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(existing));

        mockSaveCategory();

        ProfessionCategoryResponse result =
                professionCategoryService.updateProfessionCategory(
                        "PCAT-001",
                        request(null, "New description")
                );

        assertThat(result.name()).isEqualTo("Keep this name");
        assertThat(result.description()).isEqualTo("New description");

        assertThat(captureAuditLogs())
                .singleElement()
                .satisfies(log -> {
                    assertThat(log.getFieldName()).isEqualTo("description");
                    assertThat(log.getOldValue()).isEqualTo("Old description");
                    assertThat(log.getNewValue()).isEqualTo("New description");
                });
    }

    @Test
    void shouldNotWriteAuditEntriesWhenValuesAreUnchanged() {
        ProfessionCategory existing = category(
                "PCAT-001", "Development", "Development roles", true
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(existing));

        mockSaveCategory();

        ProfessionCategoryResponse result =
                professionCategoryService.updateProfessionCategory(
                        "PCAT-001",
                        request("Development", "Development roles")
                );

        assertThat(result.name()).isEqualTo("Development");

        verify(professionCategoryRepository).save(existing);
        verify(auditLogRepository, never()).saveAll(anyList());
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void shouldAllowAnEmptyUpdateWithoutWritingAuditEntries() {
        ProfessionCategory existing = category(
                "PCAT-001", "Development", "Development roles", true
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(existing));

        mockSaveCategory();

        ProfessionCategoryResponse result =
                professionCategoryService.updateProfessionCategory(
                        "PCAT-001",
                        request(null, null)
                );

        assertThat(result.name()).isEqualTo("Development");
        assertThat(result.description()).isEqualTo("Development roles");

        verify(professionCategoryRepository).save(existing);
        verify(auditLogRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownCategory() {
        when(professionCategoryRepository.findById("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                professionCategoryService.updateProfessionCategory(
                        "UNKNOWN",
                        request("New name", null)
                ))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertStatus(
                                (ResponseStatusException) exception,
                                HttpStatus.NOT_FOUND
                        ));

        verify(professionCategoryRepository, never()).save(any());
        verify(auditLogRepository, never()).saveAll(anyList());
    }

    // -------------------------------------------------------------------------
    // Status toggle
    // -------------------------------------------------------------------------

    @Test
    void shouldActivateInactiveCategory() {
        ProfessionCategory inactive = category(
                "PCAT-001", "Development", "Development roles", false
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(inactive));

        mockSaveCategory();

        ProfessionCategoryResponse result =
                professionCategoryService.toggleProfessionCategoryStatus("PCAT-001");

        assertThat(result.active()).isTrue();

        verify(professionCategoryRepository).save(argThat(ProfessionCategory::isActive));
    }

    @Test
    void shouldDeactivateActiveCategory() {
        ProfessionCategory active = category(
                "PCAT-001", "Development", "Development roles", true
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(active));

        mockSaveCategory();

        ProfessionCategoryResponse result =
                professionCategoryService.toggleProfessionCategoryStatus("PCAT-001");

        assertThat(result.active()).isFalse();

        verify(professionCategoryRepository).save(
                argThat(category -> !category.isActive())
        );
    }

    @Test
    void shouldAuditTheActualStatusChange() {
        ProfessionCategory inactive = category(
                "PCAT-001", "Development", "Development roles", false
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(inactive));

        mockSaveCategory();

        professionCategoryService.toggleProfessionCategoryStatus("PCAT-001");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);

        verify(auditLogRepository).save(captor.capture());

        AuditLog log = captor.getValue();

        assertThat(log.getAction()).isEqualTo("UPDATE");
        assertThat(log.getEntityId()).isEqualTo("PCAT-001");
        assertThat(log.getFieldName()).isEqualTo("active");
        assertThat(log.getOldValue()).isEqualTo("false");
        assertThat(log.getNewValue()).isEqualTo("true");
    }

    @Test
    void shouldReturnNotFoundWhenTogglingUnknownCategory() {
        when(professionCategoryRepository.findById("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                professionCategoryService.toggleProfessionCategoryStatus("UNKNOWN"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertStatus(
                                (ResponseStatusException) exception,
                                HttpStatus.NOT_FOUND
                        ));

        verify(professionCategoryRepository, never()).save(any());
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    @Test
    void shouldDeleteCategoryWhenNoProfessionUsesIt() {
        ProfessionCategory existing = category(
                "PCAT-001", "Development", "Development roles", true
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(existing));

        when(professionRepository.existsByCategoryId("PCAT-001"))
                .thenReturn(false);

        professionCategoryService.deleteProfessionCategory("PCAT-001");

        verify(professionCategoryRepository).delete(existing);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);

        verify(auditLogRepository).save(captor.capture());

        assertThat(captor.getValue().getAction()).isEqualTo("DELETE");
        assertThat(captor.getValue().getEntityId()).isEqualTo("PCAT-001");
        assertThat(captor.getValue().getFieldName()).isEqualTo("professionCategory");
        assertThat(captor.getValue().getOldValue()).isEqualTo("Development");
        assertThat(captor.getValue().getNewValue()).isNull();
    }

    @Test
    void shouldNotDeleteCategoryUsedByAProfession() {
        ProfessionCategory existing = category(
                "PCAT-001", "Development", "Development roles", true
        );

        when(professionCategoryRepository.findById("PCAT-001"))
                .thenReturn(Optional.of(existing));

        when(professionRepository.existsByCategoryId("PCAT-001"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                professionCategoryService.deleteProfessionCategory("PCAT-001"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertStatus(
                                (ResponseStatusException) exception,
                                HttpStatus.CONFLICT
                        ));

        verify(professionCategoryRepository, never()).delete(any());
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownCategory() {
        when(professionCategoryRepository.findById("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                professionCategoryService.deleteProfessionCategory("UNKNOWN"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertStatus(
                                (ResponseStatusException) exception,
                                HttpStatus.NOT_FOUND
                        ));

        verify(professionRepository, never()).existsByCategoryId(any());
        verify(professionCategoryRepository, never()).delete(any());
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }
}