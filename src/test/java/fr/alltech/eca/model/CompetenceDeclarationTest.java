package fr.alltech.eca.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CompetenceDeclarationTest {

    @Test
    void shouldAcceptCatalogueCompetenceDeclaration() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                "2", null, false, false, "catalogue"
        );

        assertThatCode(declaration::validate).doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptCustomerCompetenceDeclaration() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                null, "Java", false, false, "collaborator"
        );

        assertThatCode(declaration::validate).doesNotThrowAnyException();
    }

    @Test
    void shouldRejectDeclarationWithoutCompetenceInformation() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                null, null, false, false, "catalogue"
        );

        assertThatThrownBy(declaration::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectDeclarationWithBothCompetenceFields() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                "2", "Java", false, false, "catalogue"
        );

        assertThatThrownBy(declaration::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectBlankCompetenceId() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                " ", null, false, false, "catalogue"
        );

        assertThatThrownBy(declaration::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectBlankCustomName() {
        CompetenceDeclaration declaration = new CompetenceDeclaration(
                null, " ", false, false, "catalogue"
        );

        assertThatThrownBy(declaration::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }
}
