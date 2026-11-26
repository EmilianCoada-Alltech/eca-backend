package fr.alltech.eca.model.collaborator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CollaboratorTest {

    // Rule 2: Maximum 3 expertise declarations

    @Test
    void shouldAcceptCollaboratorWithNoExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(false, false),
                competence(false, false)
        ));

        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptCollaboratorWithOneExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(true, false),
                competence(false, false)
        ));

        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptCollaboratorWithThreeExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(true, false),
                competence(true, false),
                competence(true, false)
        ));

        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldRejectCollaboratorWithMoreThanThreeExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(true, false),
                competence(true, false),
                competence(true, false),
                competence(true, false)
        ));

        assertThatThrownBy(collaborator::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A collaborator cannot declare more than 3 areas of expertise.");
    }

    // Rule 3: Maximum 2 solicitable declarations

    @Test
    void shouldAcceptCollaboratorWithTwoSolicitableCompetences() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(false, true),
                competence(false, true),
                competence(false, false)
        ));

        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptCollaboratorWithNoSolicitableCompetences() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(false, false),
                competence(false, false)
        ));

        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldRejectCollaboratorWithMoreThanTwoSolicitableCompetences() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(false, true),
                competence(false, true),
                competence(false, true)
        ));

        assertThatThrownBy(collaborator::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A collaborator cannot declare more than 2 solicitable competences.");
    }

    // Test helper

    private CompetenceDeclaration competence(boolean expertise, boolean solicitable) {
        return new CompetenceDeclaration(
                "COMP-001",
                null,
                expertise,
                solicitable,
                "catalogue"
        );
    }
}