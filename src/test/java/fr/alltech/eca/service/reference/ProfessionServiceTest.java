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

/**
 * Tests unitaires du service ProfessionService.
 * Vérifie la gestion des professions : consultation, création, modification
 * et désactivation, ainsi que la validation des catégories et la génération
 * des journaux d'audit associés aux opérations.
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

    private ProfessionService professionService;

    /**
     * Initialise le service avec les dépendances simulées
     * et une instance réelle du mapper.
     */
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

    /**
     * Vérifie que le service retourne les professions actives
     * sous forme de DTO ProfessionResponse.
     */
    @Test
    void shouldReturnActiveProfessionsAsResponses() {
        // Préparation : crée une profession active.
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services.",
                true,
                "4"
        );

        // Simulation : configure le repository pour retourner cette profession.
        when(professionRepository.findByActiveTrue())
                .thenReturn(List.of(profession));

        // Exécution : récupère les professions actives via le service.
        List<ProfessionResponse> result =
                professionService.getAllActiveProfessions();

        // Vérification : contrôle le nombre de résultats et les données du DTO.
        assertThat(result).hasSize(1);

        ProfessionResponse response = result.get(0);

        assertThat(response.name()).isEqualTo("Backend developer");
        assertThat(response.description())
                .isEqualTo("Develops backend services.");
        assertThat(response.categoryId()).isEqualTo("4");
        assertThat(response.active()).isTrue();

        // Vérification : confirme que le repository a été interrogé.
        verify(professionRepository).findByActiveTrue();
    }

    /**
     * Vérifie que le service retourne une liste vide
     * lorsqu'aucune profession active n'est disponible.
     */
    @Test
    void shouldReturnEmptyListWhenNoActiveProfessionsExist() {
        // Simulation : configure le repository pour retourner une liste vide.
        when(professionRepository.findByActiveTrue())
                .thenReturn(List.of());

        // Exécution : récupère les professions actives via le service.
        List<ProfessionResponse> result =
                professionService.getAllActiveProfessions();

        // Vérification : confirme que le résultat est vide.
        assertThat(result).isEmpty();

        // Vérification : confirme que le repository a été interrogé.
        verify(professionRepository).findByActiveTrue();
    }

    /**
     * Vérifie que le service retourne toutes les professions,
     * y compris celles dont le statut est inactif.
     */
    @Test
    void shouldReturnAllProfessionsIncludingInactiveOnes() {
        // Préparation : crée une profession active et une profession inactive.
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

        // Simulation : configure le repository pour retourner les deux professions.
        when(professionRepository.findAll())
                .thenReturn(List.of(activeProfession, inactiveProfession));

        // Exécution : récupère toutes les professions via le service.
        List<ProfessionResponse> result =
                professionService.getAllProfessions();

        // Vérification : confirme que les deux professions sont retournées
        // et que leurs statuts respectifs sont conservés.
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ProfessionResponse::active)
                .containsExactly(true, false);

        // Vérification : contrôle également l'ordre et les noms des résultats.
        assertThat(result)
                .extracting(ProfessionResponse::name)
                .containsExactly("Backend developer", "Legacy developer");

        // Vérification : confirme que le repository a été interrogé.
        verify(professionRepository).findAll();
    }

    /**
     * Vérifie que le service retourne une profession existante
     * à partir de son identifiant, y compris si elle est inactive.
     */
    @Test
    void shouldReturnProfessionById() {
        // Préparation : crée une profession inactive avec un identifiant défini.
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services.",
                false,
                "4"
        );
        profession.setId("PROF-003");

        // Simulation : configure le repository pour retrouver cette profession.
        when(professionRepository.findById("PROF-003"))
                .thenReturn(Optional.of(profession));

        // Exécution : récupère la profession à partir de son identifiant.
        ProfessionResponse result =
                professionService.getProfessionById("PROF-003");

        // Vérification : contrôle les informations retournées par le service.
        assertThat(result.id()).isEqualTo("PROF-003");
        assertThat(result.name()).isEqualTo("Backend developer");
        assertThat(result.categoryId()).isEqualTo("4");
        assertThat(result.active()).isFalse();

        // Vérification : confirme que la recherche a été effectuée avec le bon identifiant.
        verify(professionRepository).findById("PROF-003");
    }

    /**
     * Vérifie que le service retourne une erreur HTTP 404
     * lorsqu'une profession demandée n'existe pas.
     */
    @Test
    void shouldThrowNotFoundWhenProfessionDoesNotExist() {
        // Simulation : configure le repository pour ne trouver aucune profession.
        when(professionRepository.findById("PROF-999"))
                .thenReturn(Optional.empty());

        // Exécution et vérification : confirme que le service lève une erreur 404.
        assertThatThrownBy(() -> professionService.getProfessionById("PROF-999"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertThat(((ResponseStatusException) exception).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );

        // Vérification : confirme que la recherche a été effectuée avec le bon identifiant.
        verify(professionRepository).findById("PROF-999");
    }

    /**
     * Vérifie la création d'une profession avec génération automatique
     * de son identifiant et création des entrées correspondantes dans le journal d'audit.
     */
    @Test
    void shouldCreateProfessionAndRecordAuditEntries() {
        // Préparation : définit les données de la nouvelle profession.
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "Develops backend services.",
                "CAT-001"
        );

        // Préparation : crée la catégorie à laquelle la profession sera associée.
        ProfessionCategory category = new ProfessionCategory(
                "IT",
                "Information technology",
                true
        );

        category.setId("CAT-001");

        // Simulation : configure la recherche de la catégorie.
        when(professionCategoryRepository.findById("CAT-001"))
                .thenReturn(Optional.of(category));

        // Simulation : configure le générateur pour retourner un identifiant déterminé.
        when(sequenceGenerator.generate(
                SequenceDefinitions.PROFESSION_SEQUENCE,
                SequenceDefinitions.PROFESSION_PREFIX,
                SequenceDefinitions.PROFESSION_INITIAL_VALUE
        )).thenReturn("PROF-063");

        // Simulation : retourne directement l'objet transmis au repository lors de l'enregistrement.
        when(professionRepository.save(any(Profession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution : crée la profession via le service.
        ProfessionResponse result = professionService.createProfession(request);

        // Vérification : contrôle les données de la profession créée.
        assertThat(result.id()).isEqualTo("PROF-063");
        assertThat(result.name()).isEqualTo("Backend developer");
        assertThat(result.description()).isEqualTo("Develops backend services.");
        assertThat(result.categoryId()).isEqualTo("CAT-001");
        assertThat(result.active()).isTrue();

        // Vérification : confirme que la catégorie a été recherchée.
        verify(professionCategoryRepository).findById("CAT-001");

        // Vérification : confirme que le générateur de séquence a été appelé
        // avec la configuration prévue pour les professions.
        verify(sequenceGenerator).generate(
                SequenceDefinitions.PROFESSION_SEQUENCE,
                SequenceDefinitions.PROFESSION_PREFIX,
                SequenceDefinitions.PROFESSION_INITIAL_VALUE
        );

        // Vérification : confirme que la profession a été enregistrée.
        verify(professionRepository).save(any(Profession.class));

        // Capture : récupère les entrées d'audit transmises au repository.
        ArgumentCaptor<List<AuditLog>> auditCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(auditLogRepository).saveAll(auditCaptor.capture());

        List<AuditLog> auditLogs = auditCaptor.getValue();

        // Vérification : contrôle le nombre d'entrées d'audit,
        // leur type d'action, leur identifiant métier et leur auteur.
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

    /**
     * Vérifie que la création d'une profession échoue avec une erreur HTTP 404
     * lorsque la catégorie demandée n'existe pas.
     */
    @Test
    void shouldThrowNotFoundWhenProfessionCategoryDoesNotExist() {
        // Préparation : définit une profession associée à une catégorie inexistante.
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "Develops backend services.",
                "CAT-999"
        );

        // Simulation : configure le repository pour ne trouver aucune catégorie.
        when(professionCategoryRepository.findById("CAT-999"))
                .thenReturn(Optional.empty());

        // Exécution et vérification : confirme que le service lève une erreur 404.
        assertThatThrownBy(() -> professionService.createProfession(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertThat(((ResponseStatusException) exception).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );

        // Vérification : confirme que la catégorie a été recherchée.
        verify(professionCategoryRepository).findById("CAT-999");

        // Vérification : confirme qu'aucune génération d'identifiant,
        // persistance de profession ou création d'audit n'a été effectuée.
        verifyNoInteractions(sequenceGenerator, professionRepository, auditLogRepository);
    }

    /**
     * Vérifie la modification d'une profession existante et la création
     * des entrées d'audit correspondant aux champs modifiés.
     */
    @Test
    void shouldUpdateProfessionAndRecordChangedFields() {
        // Préparation : crée une profession existante avec ses anciennes valeurs.
        Profession profession = new Profession(
                "Backend developer",
                "Old description.",
                true,
                "CAT-001"
        );
        profession.setId("PROF-001");

        // Préparation : définit les nouvelles valeurs à appliquer.
        ProfessionRequest request = new ProfessionRequest(
                "Senior backend developer",
                "New description.",
                "CAT-002"
        );

        // Préparation : crée la nouvelle catégorie associée à la profession.
        ProfessionCategory category = new ProfessionCategory(
                "Development",
                "Development professions",
                true
        );
        category.setId("CAT-002");

        // Simulation : configure la récupération de la profession existante.
        when(professionRepository.findById("PROF-001"))
                .thenReturn(Optional.of(profession));

        // Simulation : configure la récupération de la nouvelle catégorie.
        when(professionCategoryRepository.findById("CAT-002"))
                .thenReturn(Optional.of(category));

        // Simulation : retourne l'objet transmis au repository lors de l'enregistrement.
        when(professionRepository.save(any(Profession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution : met à jour la profession via le service.
        ProfessionResponse result =
                professionService.updateProfession("PROF-001", request);

        // Vérification : contrôle les nouvelles valeurs de la profession.
        assertThat(result.id()).isEqualTo("PROF-001");
        assertThat(result.name()).isEqualTo("Senior backend developer");
        assertThat(result.description()).isEqualTo("New description.");
        assertThat(result.categoryId()).isEqualTo("CAT-002");
        assertThat(result.active()).isTrue();

        // Capture : récupère les entrées d'audit générées par la modification.
        ArgumentCaptor<List<AuditLog>> auditCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(auditLogRepository).saveAll(auditCaptor.capture());

        List<AuditLog> auditLogs = auditCaptor.getValue();

        // Vérification : confirme que trois entrées d'audit ont été créées
        // pour cette profession et qu'elles correspondent à des mises à jour.
        assertThat(auditLogs).hasSize(3);
        assertThat(auditLogs)
                .extracting(AuditLog::getAction)
                .containsOnly("UPDATE");

        assertThat(auditLogs)
                .extracting(AuditLog::getEntityId)
                .containsOnly("PROF-001");

        // Vérification : confirme que la profession existante a été enregistrée.
        verify(professionRepository).save(profession);
    }

    /**
     * Vérifie que seules les propriétés réellement modifiées
     * génèrent des entrées dans le journal d'audit.
     */
    @Test
    void shouldOnlyAuditFieldsThatChanged() {
        // Préparation : crée une profession avec ses valeurs initiales.
        Profession profession = new Profession(
                "Backend developer",
                "Old description.",
                true,
                "CAT-001"
        );
        profession.setId("PROF-001");

        // Préparation : conserve le nom et la catégorie,
        // mais définit une nouvelle description.
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "New description.",
                "CAT-001"
        );

        // Préparation : crée la catégorie associée à la profession.
        ProfessionCategory category = new ProfessionCategory(
                "IT",
                "Information technology",
                true
        );

        // Simulation : configure la récupération de la profession et de sa catégorie.
        when(professionRepository.findById("PROF-001"))
                .thenReturn(Optional.of(profession));

        when(professionCategoryRepository.findById("CAT-001"))
                .thenReturn(Optional.of(category));

        // Simulation : retourne l'objet transmis au repository lors de l'enregistrement.
        when(professionRepository.save(any(Profession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution : met à jour la profession via le service.
        ProfessionResponse result =
                professionService.updateProfession("PROF-001", request);

        // Vérification : confirme que la nouvelle description est appliquée.
        assertThat(result.description()).isEqualTo("New description.");

        // Capture : récupère les entrées d'audit générées par la modification.
        ArgumentCaptor<List<AuditLog>> auditCaptor =
                ArgumentCaptor.forClass(List.class);

        verify(auditLogRepository).saveAll(auditCaptor.capture());

        List<AuditLog> auditLogs = auditCaptor.getValue();

        // Vérification : confirme qu'une seule entrée d'audit a été créée
        // puisque seule la description a changé.
        assertThat(auditLogs).hasSize(1);
        assertThat(auditLogs)
                .extracting(AuditLog::getAction)
                .containsOnly("UPDATE");
    }

    /**
     * Vérifie qu'aucune entrée d'audit n'est créée
     * lorsqu'une profession est mise à jour sans changement de valeur.
     */
    @Test
    void shouldNotCreateAuditEntriesWhenNothingChanged() {
        // Préparation : crée une profession avec ses valeurs actuelles.
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services.",
                true,
                "CAT-001"
        );
        profession.setId("PROF-001");

        // Préparation : définit une requête contenant exactement les mêmes valeurs.
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "Develops backend services.",
                "CAT-001"
        );

        // Préparation : crée la catégorie associée à la profession.
        ProfessionCategory category = new ProfessionCategory(
                "IT",
                "Information technology",
                true
        );

        // Simulation : configure la récupération de la profession et de sa catégorie.
        when(professionRepository.findById("PROF-001"))
                .thenReturn(Optional.of(profession));

        when(professionCategoryRepository.findById("CAT-001"))
                .thenReturn(Optional.of(category));

        // Simulation : retourne l'objet transmis au repository lors de l'enregistrement.
        when(professionRepository.save(any(Profession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution : soumet la mise à jour sans changement de valeur.
        ProfessionResponse result =
                professionService.updateProfession("PROF-001", request);

        // Vérification : confirme que la profession retournée conserve son identifiant.
        assertThat(result.id()).isEqualTo("PROF-001");

        // Vérification : confirme que la profession est enregistrée.
        verify(professionRepository).save(profession);

        // Vérification : confirme qu'aucune entrée d'audit n'a été créée.
        verify(auditLogRepository, never()).saveAll(anyList());
    }

    /**
     * Vérifie que la modification d'une profession inexistante
     * échoue avec une erreur HTTP 404.
     */
    @Test
    void shouldThrowNotFoundWhenUpdatingNonexistentProfession() {
        // Préparation : définit les nouvelles données de la profession.
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "New description.",
                "CAT-001"
        );

        // Simulation : configure le repository pour ne trouver aucune profession.
        when(professionRepository.findById("PROF-999"))
                .thenReturn(Optional.empty());

        // Exécution et vérification : confirme que le service lève une erreur 404.
        assertThatThrownBy(() ->
                professionService.updateProfession("PROF-999", request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertThat(((ResponseStatusException) exception).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );

        // Vérification : confirme que la profession a été recherchée.
        verify(professionRepository).findById("PROF-999");

        // Vérification : confirme qu'aucune recherche de catégorie
        // ni création d'entrée d'audit n'a été effectuée.
        verifyNoInteractions(professionCategoryRepository);
        verifyNoInteractions(auditLogRepository);
    }

    /**
     * Vérifie que la modification d'une profession échoue avec une erreur HTTP 404
     * lorsque la nouvelle catégorie demandée n'existe pas.
     */
    @Test
    void shouldThrowNotFoundWhenUpdatingWithNonexistentCategory() {
        // Préparation : crée une profession existante.
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services.",
                true,
                "CAT-001"
        );
        profession.setId("PROF-001");

        // Préparation : définit une requête associant la profession
        // à une catégorie inexistante.
        ProfessionRequest request = new ProfessionRequest(
                "Backend developer",
                "New description.",
                "CAT-999"
        );

        // Simulation : configure la récupération de la profession existante.
        when(professionRepository.findById("PROF-001"))
                .thenReturn(Optional.of(profession));

        // Simulation : configure le repository pour ne trouver aucune catégorie.
        when(professionCategoryRepository.findById("CAT-999"))
                .thenReturn(Optional.empty());

        // Exécution et vérification : confirme que le service lève une erreur 404.
        assertThatThrownBy(() ->
                professionService.updateProfession("PROF-001", request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertThat(((ResponseStatusException) exception).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );

        // Vérification : confirme que la profession et la catégorie ont été recherchées.
        verify(professionRepository).findById("PROF-001");
        verify(professionCategoryRepository).findById("CAT-999");

        // Vérification : confirme qu'aucune sauvegarde de profession
        // ni création d'entrée d'audit n'a été effectuée.
        verify(professionRepository, never()).save(any(Profession.class));
        verifyNoInteractions(auditLogRepository);
    }

    /**
     * Vérifie la désactivation d'une profession existante
     * et la création d'une entrée d'audit retraçant le changement de statut.
     */
    @Test
    void shouldDeactivateProfessionAndRecordAuditEntry() {
        // Préparation : crée une profession initialement active.
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services.",
                true,
                "CAT-001"
        );
        profession.setId("PROF-001");

        // Simulation : configure la récupération de la profession existante.
        when(professionRepository.findById("PROF-001"))
                .thenReturn(Optional.of(profession));

        // Simulation : retourne l'objet transmis au repository lors de l'enregistrement.
        when(professionRepository.save(any(Profession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution : désactive la profession via le service.
        professionService.deactivateProfession("PROF-001");

        // Vérification : confirme que le statut de la profession est désormais inactif.
        assertThat(profession.isActive()).isFalse();

        // Vérification : confirme que la profession a été enregistrée.
        verify(professionRepository).save(profession);

        // Capture : récupère l'entrée d'audit créée lors de la désactivation.
        ArgumentCaptor<AuditLog> auditCaptor =
                ArgumentCaptor.forClass(AuditLog.class);

        verify(auditLogRepository).save(auditCaptor.capture());

        AuditLog auditLog = auditCaptor.getValue();

        // Vérification : contrôle les informations retraçant le changement de statut.
        assertThat(auditLog.getEntityId()).isEqualTo("PROF-001");
        assertThat(auditLog.getFieldName()).isEqualTo("active");
        assertThat(auditLog.getOldValue()).isEqualTo("true");
        assertThat(auditLog.getNewValue()).isEqualTo("false");
        assertThat(auditLog.getAction()).isEqualTo("DELETE");
    }

    /**
     * Vérifie qu'aucune sauvegarde ni entrée d'audit n'est créée
     * lorsqu'une profession est déjà inactive.
     */
    @Test
    void shouldNotCreateAuditEntryWhenProfessionIsAlreadyInactive() {
        // Préparation : crée une profession dont le statut est déjà inactif.
        Profession profession = new Profession(
                "Backend developer",
                "Develops backend services.",
                false,
                "CAT-001"
        );
        profession.setId("PROF-001");

        // Simulation : configure la récupération de la profession existante.
        when(professionRepository.findById("PROF-001"))
                .thenReturn(Optional.of(profession));

        // Exécution : demande la désactivation de la profession.
        professionService.deactivateProfession("PROF-001");

        // Vérification : confirme qu'aucune nouvelle sauvegarde
        // ni entrée d'audit n'est effectuée.
        verify(professionRepository, never()).save(any(Profession.class));
        verifyNoInteractions(auditLogRepository);
    }

    /**
     * Vérifie que la désactivation d'une profession inexistante
     * échoue avec une erreur HTTP 404.
     */
    @Test
    void shouldThrowNotFoundWhenDeactivatingNonexistentProfession() {
        // Simulation : configure le repository pour ne trouver aucune profession.
        when(professionRepository.findById("PROF-999"))
                .thenReturn(Optional.empty());

        // Exécution et vérification : confirme que le service lève une erreur 404.
        assertThatThrownBy(() ->
                professionService.deactivateProfession("PROF-999"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception ->
                        assertThat(((ResponseStatusException) exception).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );

        // Vérification : confirme que la recherche a été effectuée
        // et qu'aucune entrée d'audit n'a été créée.
        verify(professionRepository).findById("PROF-999");
        verifyNoInteractions(auditLogRepository);
    }
}