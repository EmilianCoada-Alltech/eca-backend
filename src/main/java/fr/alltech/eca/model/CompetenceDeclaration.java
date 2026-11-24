package fr.alltech.eca.model;

public class CompetenceDeclaration {

    private String competenceId;
    private String customName;
    private boolean expertise;
    private boolean solicitable;
    private String source;

    public CompetenceDeclaration() {
    }

    public CompetenceDeclaration(
            String competenceId,
            String customName,
            boolean expertise,
            boolean solicitable,
            String source
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}