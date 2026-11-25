package fr.alltech.eca.model.association;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "collaborator_missions")
public class CollaboratorMission {

    @Id
    private String id;

    private String collaboratorId;
    private String missionId;
    private LocalDate startDate;
    private LocalDate endDate;

    public CollaboratorMission() {
    }

    public CollaboratorMission(
            String collaboratorId,
            String missionId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        this.collaboratorId = collaboratorId;
        this.missionId = missionId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollaboratorId() {
        return collaboratorId;
    }

    public void setCollaboratorId(String collaboratorId) {
        this.collaboratorId = collaboratorId;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}