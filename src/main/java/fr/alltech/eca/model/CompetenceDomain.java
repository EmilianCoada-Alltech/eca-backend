package fr.alltech.eca.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "competence_domains")
public class CompetenceDomain {

    @Id
    private String id;

    private String competenceId;

    private String domainId;

    public CompetenceDomain() {
    }

    public CompetenceDomain(String competenceId, String domainId) {
        this.competenceId = competenceId;
        this.domainId = domainId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompetenceId() {
        return competenceId;
    }

    public void setCompetenceId(String competenceId) {
        this.competenceId = competenceId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }
}