
package fr.alltech.eca.model.reference;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Représente un domaine du référentiel ECA.
 */
@Document(collection = "domains")
public class Domain {

    @Id
    private String id;

    private String name;
    private String description;
    private boolean active;

    public Domain() {
    }

    public Domain(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
