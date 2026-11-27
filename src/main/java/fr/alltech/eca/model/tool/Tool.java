
package fr.alltech.eca.model.tool;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Représente un outil mis à disposition des collaborateurs dans l'application ECA,
 * associé à une catégorie et disposant d'un statut d'activité.
 */
@Document(collection = "tools")
public class Tool {

    @Id
    private String id;

    private String name;
    private String description;
    private String url;
    private String categoryId;
    private boolean active;

    public Tool() {
    }

    public Tool(
            String id,
            String name,
            String description,
            String url,
            String categoryId,
            boolean active
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.categoryId = categoryId;
        this.active = active;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
