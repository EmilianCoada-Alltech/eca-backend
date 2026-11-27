
package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.profession.ProfessionRequest;
import fr.alltech.eca.dto.reference.profession.ProfessionResponse;
import fr.alltech.eca.mapper.reference.ProfessionMapper;
import fr.alltech.eca.model.audit.AuditLog;
import fr.alltech.eca.model.reference.Profession;
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
import static org.mockito.Mockito.*;

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

    private ProfessionService professionService;

    @BeforeEach
    void setUp() {
        professionService = new ProfessionService(
                professionRepository,
                professionCategoryRepository,
                auditLogRepository,
                new ProfessionMapper(),
                sequenceGenerator
        );
    }

    @Test
    void shouldReturnActiveProfessionsAsResponses() {
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services.",
                true,
                "4"
        );

        when(professionRepository.findByActiveTrue())
                .thenReturn(List.of(profession));

        List<ProfessionResponse> result =
                professionService.getAllActiveProfessions();

        assertThat(result).hasSize(1);

        ProfessionResponse response = result.get(0);

        assertThat(response.name()).isEqualTo("Backend developer");
        assertThat(response.description())
                .isEqualTo("Develops backend services.");
        assertThat(response.categoryId()).isEqualTo("4");
        assertThat(response.active()).isTrue();

        verify(professionRepository).findByActiveTrue();
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveProfessionsExist() {
        when(professionRepository.findByActiveTrue())
                .thenReturn(List.of());

        List<ProfessionResponse> result =
                professionService.getAllActiveProfessions();

        assertThat(result).isEmpty();

        verify(professionRepository).findByActiveTrue();
    }

    @Test
    void shouldReturnAllProfessionsIncludingInactiveOnes() {
        Profession activeProfession = new Profession(
                "Backend developer",
                "Develops backend services.",
                true,
                "4"
        );

        Profession inactiveProfession = new Profession(
                "Legacy developer",
                "An inactive profession.",
                false,
                "4"
        );

        when(professionRepository.findAll())
                .thenReturn(List.of(activeProfession, inactiveProfession));

        List<ProfessionResponse> result =
                professionService.getAllProfessions();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ProfessionResponse::active)
                .containsExactly(true, false);

        assertThat(result)
                .extracting(ProfessionResponse::name)
                .containsExactly("Backend developer", "Legacy developer");

        verify(professionRepository).findAll();
    }

    @Test
    void shouldReturnProfessionById() {
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services.",
                false,
                "4"
        );
        profession.setId("PROF-003");

        when(professionRepository.findById("PROF-003"))
                .thenReturn(Optional.of(profession));

        ProfessionResponse result =
                professionService.getProfessionById("PROF-003");

        assertThat(result.id()).isEqualTo("PROF-003");
        assertThat(result.name()).isEqualTo("Backend developer");
        assertThat(result.categoryId()).isEqualTo("4");
        assertThat(result.active()).isFalse();

        verify(professionRepository).findById("PROF-003");
    }

    @Test
    void shouldThrowNotFoundWhenProfessionDoesNotExist() {
        when(professionRepository.findById("PROF-999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionService.getProfessionById("PROF-999"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertThat(((ResponseStatusException) exception).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );

        verify(professionRepository).findById("PROF-999");
    }

    @Test
    void shouldCreateProfessionAndRecordAuditEntries() {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "Develops backend services.",
                "CAT-001"
        );

        ProfessionCategory category = new ProfessionCategory(
                "IT",
                "Information technology"
        );

        category.setId("CAT-001");

        when(professionCategoryRepository.findById("CAT-001"))
                .thenReturn(Optional.of(category));

        when(sequenceGenerator.generate(
                SequenceDefinitions.PROFESSION_SEQUENCE,
                SequenceDefinitions.PROFESSION_PREFIX,
                SequenceDefinitions.PROFESSION_INITIAL_VALUE
        )).thenReturn("PROF-063");

        when(professionRepository.save(any(Profession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionResponse result = professionService.createProfession(request);

        assertThat(result.id()).isEqualTo("PROF-063");
        assertThat(result.name()).isEqualTo("Backend developer");
        assertThat(result.description()).isEqualTo("Develops backend services.");
        assertThat(result.categoryId()).isEqualTo("CAT-001");
        assertThat(result.active()).isTrue();

        verify(professionCategoryRepository).findById("CAT-001");

        verify(sequenceGenerator).generate(
                SequenceDefinitions.PROFESSION_SEQUENCE,
                SequenceDefinitions.PROFESSION_PREFIX,
                SequenceDefinitions.PROFESSION_INITIAL_VALUE
        );

        verify(professionRepository).save(any(Profession.class));

        ArgumentCaptor<List<AuditLog>> auditCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(auditLogRepository).saveAll(auditCaptor.capture());

        List<AuditLog> auditLogs = auditCaptor.getValue();

        assertThat(auditLogs).hasSize(4);
        assertThat(auditLogs)
                .extracting(AuditLog::getAction)
                .containsOnly("CREATE");

        assertThat(auditLogs)
                .extracting(AuditLog::getEntityId)
                .containsOnly("PROF-063");

        assertThat(auditLogs)
                .extracting(AuditLog::getChangedBy)
                .containsOnly("ADMIN");
    }

    @Test
    void shouldThrowNotFoundWhenProfessionCategoryDoesNotExist() {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "Develops backend services.",
                "CAT-999"
        );

        when(professionCategoryRepository.findById("CAT-999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> professionService.createProfession(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertThat(((ResponseStatusException) exception).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );

        verify(professionCategoryRepository).findById("CAT-999");

        verifyNoInteractions(sequenceGenerator, professionRepository, auditLogRepository);
    }

    @Test
    void shouldUpdateProfessionAndRecordChangedFields() {
        Profession profession = new Profession(
                "Backend developer",
                "Old description.",
                true,
                "CAT-001"
        );
        profession.setId("PROF-001");

        ProfessionRequest request = new ProfessionRequest(
                "Senior backend developer",
                "New description.",
                "CAT-002"
        );

        ProfessionCategory category = new ProfessionCategory(
                "Development",
                "Development professions"
        );
        category.setId("CAT-002");

        when(professionRepository.findById("PROF-001"))
                .thenReturn(Optional.of(profession));

        when(professionCategoryRepository.findById("CAT-002"))
                .thenReturn(Optional.of(category));

        when(professionRepository.save(any(Profession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionResponse result =
                professionService.updateProfession("PROF-001", request);

        assertThat(result.id()).isEqualTo("PROF-001");
        assertThat(result.name()).isEqualTo("Senior backend developer");
        assertThat(result.description()).isEqualTo("New description.");
        assertThat(result.categoryId()).isEqualTo("CAT-002");
        assertThat(result.active()).isTrue();

        ArgumentCaptor<List<AuditLog>> auditCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(auditLogRepository).saveAll(auditCaptor.capture());

        List<AuditLog> auditLogs = auditCaptor.getValue();

        assertThat(auditLogs).hasSize(3);
        assertThat(auditLogs)
                .extracting(AuditLog::getAction)
                .containsOnly("UPDATE");

        assertThat(auditLogs)
                .extracting(AuditLog::getEntityId)
                .containsOnly("PROF-001");

        verify(professionRepository).save(profession);
    }

    @Test
    void shouldOnlyAuditFieldsThatChanged() {
        Profession profession = new Profession(
                "Backend developer",
                "Old description.",
                true,
                "CAT-001"
        );
        profession.setId("PROF-001");

        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "New description.",
                "CAT-001"
        );

        ProfessionCategory category = new ProfessionCategory(
                "IT",
                "Information technology"
        );

        when(professionRepository.findById("PROF-001"))
                .thenReturn(Optional.of(profession));

        when(professionCategoryRepository.findById("CAT-001"))
                .thenReturn(Optional.of(category));

        when(professionRepository.save(any(Profession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionResponse result =
                professionService.updateProfession("PROF-001", request);

        assertThat(result.description()).isEqualTo("New description.");

        ArgumentCaptor<List<AuditLog>> auditCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(auditLogRepository).saveAll(auditCaptor.capture());

        List<AuditLog> auditLogs = auditCaptor.getValue();

        assertThat(auditLogs).hasSize(1);
        assertThat(auditLogs)
                .extracting(AuditLog::getAction)
                .containsOnly("UPDATE");
    }

    @Test
    void shouldNotCreateAuditEntriesWhenNothingChanged() {
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services.",
                true,
                "CAT-001"
        );
        profession.setId("PROF-001");

        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "Develops backend services.",
                "CAT-001"
        );

        ProfessionCategory category = new ProfessionCategory(
                "IT",
                "Information technology"
        );

        when(professionRepository.findById("PROF-001"))
                .thenReturn(Optional.of(profession));

        when(professionCategoryRepository.findById("CAT-001"))
                .thenReturn(Optional.of(category));

        when(professionRepository.save(any(Profession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfessionResponse result =
                professionService.updateProfession("PROF-001", request);

        assertThat(result.id()).isEqualTo("PROF-001");

        verify(professionRepository).save(profession);

        verify(auditLogRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonexistentProfession() {
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "New description.",
                "CAT-001"
        );

        when(professionRepository.findById("PROF-999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                professionService.updateProfession("PROF-999", request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertThat(((ResponseStatusException) exception).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );

        verify(professionRepository).findById("PROF-999");

        verifyNoInteractions(professionCategoryRepository);
        verifyNoInteractions(auditLogRepository);
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingWithNonexistentCategory() {
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services.",
                true,
                "CAT-001"
        );
        profession.setId("PROF-001");

        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "New description.",
                "CAT-999"
        );

        when(professionRepository.findById("PROF-001"))
                .thenReturn(Optional.of(profession));

        when(professionCategoryRepository.findById("CAT-999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                professionService.updateProfession("PROF-001", request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertThat(((ResponseStatusException) exception).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );

        verify(professionRepository).findById("PROF-001");
        verify(professionCategoryRepository).findById("CAT-999");

        verify(professionRepository, never()).save(any(Profession.class));
        verifyNoInteractions(auditLogRepository);
    }
}
