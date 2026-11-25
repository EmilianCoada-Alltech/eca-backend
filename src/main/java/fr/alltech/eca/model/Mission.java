package fr.alltech.eca.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "missions")
public class Mission {

    @Id
    private String id;

    private String name;
    private String clientId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private boolean isVisible;

    public Mission() {
    }

    public Mission(
            String name,
            String clientId,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            boolean isVisible
    ) {
        this.name = name;
        this.clientId = clientId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.isVisible = isVisible;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}