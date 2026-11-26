package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.ProfessionResponse;
import fr.alltech.eca.mapper.reference.ProfessionMapper;
import fr.alltech.eca.model.reference.Profession;
import fr.alltech.eca.repository.reference.ProfessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessionServiceTest {

    @Mock
    private ProfessionRepository professionRepository;

    private ProfessionService professionService;

    @BeforeEach
    void setUp() {
        professionService = new ProfessionService(
                professionRepository,
                new ProfessionMapper()
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

}