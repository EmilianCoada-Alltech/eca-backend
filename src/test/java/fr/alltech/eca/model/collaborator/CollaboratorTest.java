package fr.alltech.eca.model.collaborator;

import fr.alltech.eca.common.enums.CompetenceSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitaires des règles de validation du modèle Collaborator.
 * Vérifie notamment les limites concernant les déclarations d'expertise
 * et les compétences pour lesquelles un collaborateur accepte d'être sollicité.
 */
class CollaboratorTest {

    // Règle 2 : maximum de 3 déclarations d'expertise.

    /**
     * Vérifie qu'un collaborateur peut ne déclarer aucune expertise.
     */
    @Test
    void shouldAcceptCollaboratorWithNoExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(false, false),
                competence(false, false)
        ));

        // Vérification : aucune exception ne doit être levée.
        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    /**
     * Vérifie qu'un collaborateur peut déclarer une seule expertise.
     */
    @Test
    void shouldAcceptCollaboratorWithOneExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(true, false),
                competence(false, false)
        ));

        // Vérification : une déclaration d'expertise est autorisée.
        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    /**
     * Vérifie qu'un collaborateur peut déclarer exactement trois expertises.
     */
    @Test
    void shouldAcceptCollaboratorWithThreeExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(true, false),
                competence(true, false),
                competence(true, false)
        ));

        // Vérification : la limite maximale est respectée.
        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    /**
     * Vérifie qu'une validation échoue lorsque le collaborateur
     * déclare plus de trois expertises.
     */
    @Test
    void shouldRejectCollaboratorWithMoreThanThreeExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(true, false),
                competence(true, false),
                competence(true, false),
                competence(true, false)
        ));

        // Vérification : une exception est levée lorsque la limite est dépassée.
        assertThatThrownBy(collaborator::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A collaborator cannot declare more than 3 areas of expertise.");
    }

    // Règle 3 : maximum de 2 compétences pour lesquelles le collaborateur est sollicitable.

    /**
     * Vérifie qu'un collaborateur peut déclarer exactement deux compétences
     * pour lesquelles il accepte d'être sollicité.
     */
    @Test
    void shouldAcceptCollaboratorWithTwoSolicitableCompetences() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(false, true),
                competence(false, true),
                competence(false, false)
        ));

        // Vérification : la limite maximale est respectée.
        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    /**
     * Vérifie qu'un collaborateur peut ne déclarer aucune compétence
     * pour laquelle il accepte d'être sollicité.
     */
    @Test
    void shouldAcceptCollaboratorWithNoSolicitableCompetences() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(false, false),
                competence(false, false)
        ));

        // Vérification : aucune exception ne doit être levée.
        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    /**
     * Vérifie qu'une validation échoue lorsque le collaborateur
     * déclare plus de deux compétences pour lesquelles il est sollicitable.
     */
    @Test
    void shouldRejectCollaboratorWithMoreThanTwoSolicitableCompetences() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(false, true),
                competence(false, true),
                competence(false, true)
        ));

        // Vérification : une exception est levée lorsque la limite est dépassée.
        assertThatThrownBy(collaborator::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A collaborator cannot declare more than 2 solicitable competences.");
    }

    // Méthode utilitaire de création des déclarations de compétences.

    /**
     * Crée une déclaration de compétence avec les indicateurs fournis.
     *
     * @param expertise   indique si la compétence est déclarée comme expertise
     * @param solicitable indique si le collaborateur accepte d'être sollicité pour cette compétence
     * @return une déclaration de compétence issue du catalogue
     */
    private CompetenceDeclaration competence(boolean expertise, boolean solicitable) {
        return new CompetenceDeclaration(
                "COMP-001",
                null,
                expertise,
                solicitable,
                CompetenceSource.CATALOGUE
        );
    }
}