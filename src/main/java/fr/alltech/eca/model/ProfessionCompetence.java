package fr.alltech.eca.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profession_competences")
public class ProfessionCompetence {

    @Id
    private String id;

    private String professionId;

    private String competenceId;

    public ProfessionCompetence() {
    }

    public ProfessionCompetence(String professionId, String competenceId) {
        this.professionId = professionId;
        this.competenceId = competenceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfessionId() {
        return professionId;
    }

    public void setProfessionId(String professionId) {
        this.professionId = professionId;
    }

    public String getCompetenceId() {
        return competenceId;
    }

    public void setCompetenceId(String competenceId) {
        this.competenceId = competenceId;
    }
}