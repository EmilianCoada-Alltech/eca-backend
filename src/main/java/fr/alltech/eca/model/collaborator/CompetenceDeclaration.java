package fr.alltech.eca.model.collaborator;

import fr.alltech.eca.common.enums.CompetenceSource;

/**
 * Représente une compétence déclarée par un collaborateur.
 */
public class CompetenceDeclaration {

    private String competenceId;
    private String customName;
    private boolean expertise;
    private boolean solicitable;
    private CompetenceSource source;

    public CompetenceDeclaration() {
    }

    public CompetenceDeclaration(
            String competenceId,
            String customName,
            boolean expertise,
            boolean solicitable,
            CompetenceSource source
    ) {
        this.competenceId = competenceId;
        this.customName = customName;
        this.expertise = expertise;
        this.solicitable = solicitable;
        this.source = source;
    }

    public String getCompetenceId() {
        return competenceId;
    }

    public void setCompetenceId(String competenceId) {
        this.competenceId = competenceId;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public boolean isExpertise() {
        return expertise;
    }

    public void setExpertise(boolean expertise) {
        this.expertise = expertise;
    }

    public boolean isSolicitable() {
        return solicitable;
    }

    public void setSolicitable(boolean solicitable) {
        this.solicitable = solicitable;
    }

    public CompetenceSource getSource() {
        return source;
    }

    public void setSource(CompetenceSource source) {
        this.source = source;
    }

    /**
     * Vérifie qu'une déclaration référence une compétence existante
     * ou renseigne un nom personnalisé, mais jamais les deux simultanément.
     */
    public void validate() {
        boolean hasCompetenceId =
                competenceId != null && !competenceId.isBlank();

        boolean hasCustomName =
                customName != null && !customName.isBlank();

        if (hasCompetenceId == hasCustomName) {
            throw new IllegalArgumentException(
                    "A declaration must reference a competence or provide a custom name, but not both."
            );
        }
    }
}