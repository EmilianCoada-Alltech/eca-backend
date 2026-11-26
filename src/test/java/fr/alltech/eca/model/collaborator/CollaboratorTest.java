package fr.alltech.eca.model.collaborator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CollaboratorTest {

    @Test
    void shouldAcceptCollaboratorWithNoExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(false),
                competence(false)
        ));

        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptCollaboratorWithOneExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(true),
                competence(false)
        ));

        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptCollaboratorWithThreeExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(true),
                competence(true),
                competence(true)
        ));

        assertThatCode(collaborator::validate)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldRejectCollaboratorWithMoreThanThreeExpertise() {
        Collaborator collaborator = new Collaborator();
        collaborator.setCompetences(List.of(
                competence(true),
                competence(true),
                competence(true),
                competence(true)
        ));

        assertThatThrownBy(collaborator::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A collaborator cannot declare more than 3 areas of expertise.");
    }

    private CompetenceDeclaration competence(boolean expertise) {
        return new CompetenceDeclaration(
                "COMP-001",
                null,
                expertise,
                false,
                "catalogue"
        );
    }
}