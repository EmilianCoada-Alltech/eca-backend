package fr.alltech.eca.model.collaborator;

import fr.alltech.eca.common.enums.CompetenceSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitaires de validation du modèle CompetenceDeclaration.
 * Vérifie les règles de cohérence entre une compétence issue du catalogue
 * et une compétence déclarée librement par un collaborateur.
 */
public class CompetenceDeclarationTest {

    /**
     * Vérifie qu'une déclaration de compétence issue du catalogue
     * est acceptée lorsqu'elle possède un identifiant valide.
     */
    @Test
    void shouldAcceptCatalogueCompetenceDeclaration() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                "2", null, false, false, CompetenceSource.CATALOGUE
        );

        // Vérification : la déclaration doit être considérée comme valide.
        assertThatCode(declaration::validate).doesNotThrowAnyException();
    }

    /**
     * Vérifie qu'une compétence déclarée librement par le collaborateur
     * est acceptée lorsqu'elle possède un nom valide.
     */
    @Test
    void shouldAcceptCustomerCompetenceDeclaration() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                null, "Java", false, false, CompetenceSource.USER
        );

        // Vérification : la déclaration doit être considérée comme valide.
        assertThatCode(declaration::validate).doesNotThrowAnyException();
    }

    /**
     * Vérifie qu'une déclaration est rejetée lorsqu'aucune information
     * sur la compétence n'est fournie.
     */
    @Test
    void shouldRejectDeclarationWithoutCompetenceInformation() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                null, null, false, false, CompetenceSource.CATALOGUE
        );

        // Vérification : une exception doit être levée en l'absence d'information.
        assertThatThrownBy(declaration::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Vérifie qu'une déclaration est rejetée lorsque les deux champs
     * d'identification de la compétence sont renseignés simultanément.
     */
    @Test
    void shouldRejectDeclarationWithBothCompetenceFields() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                "2", "Java", false, false, CompetenceSource.CATALOGUE
        );

        // Vérification : les deux modes d'identification sont incompatibles.
        assertThatThrownBy(declaration::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Vérifie qu'un identifiant de compétence composé uniquement d'espaces
     * est rejeté.
     */
    @Test
    void shouldRejectBlankCompetenceId() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                " ", null, false, false, CompetenceSource.CATALOGUE
        );

        // Vérification : un identifiant vide ou blanc n'est pas accepté.
        assertThatThrownBy(declaration::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Vérifie qu'un nom de compétence personnalisé composé uniquement
     * d'espaces est rejeté.
     */
    @Test
    void shouldRejectBlankCustomName() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                null, " ", false, false, CompetenceSource.CATALOGUE
        );

        // Vérification : un nom vide ou blanc n'est pas accepté.
        assertThatThrownBy(declaration::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }
}