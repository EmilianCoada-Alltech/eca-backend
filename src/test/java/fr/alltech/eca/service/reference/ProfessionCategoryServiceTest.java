
package fr.alltech.eca.service.reference;

import fr.alltech.eca.dto.reference.ProfessionCategoryResponse;
import fr.alltech.eca.mapper.reference.ProfessionCategoryMapper;
import fr.alltech.eca.model.reference.ProfessionCategory;
import fr.alltech.eca.repository.reference.ProfessionCategoryRepository;
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
class ProfessionCategoryServiceTest {

    @Mock
    private ProfessionCategoryRepository professionCategoryRepository;

    private ProfessionCategoryService professionCategoryService;

    @BeforeEach
    void setUp() {
        professionCategoryService = new ProfessionCategoryService(
                professionCategoryRepository,
                new ProfessionCategoryMapper()
        );
    }

    @Test
    void shouldReturnProfessionCategoriesAsResponses() {
        ProfessionCategory category = new ProfessionCategory(
                "Development",
                "Groups professions related to software development."
        );

        when(professionCategoryRepository.findAll())
                .thenReturn(List.of(category));

        List<ProfessionCategoryResponse> result =
                professionCategoryService.getAllProfessionCategories();

        assertThat(result).hasSize(1);

        ProfessionCategoryResponse response = result.get(0);

        assertThat(response.name()).isEqualTo("Development");
        assertThat(response.description())
                .isEqualTo("Groups professions related to software development.");

        verify(professionCategoryRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoProfessionCategoriesExist() {
        when(professionCategoryRepository.findAll())
                .thenReturn(List.of());

        List<ProfessionCategoryResponse> result =
                professionCategoryService.getAllProfessionCategories();

        assertThat(result).isEmpty();

        verify(professionCategoryRepository).findAll();
    }
}
