
package fr.alltech.eca.model.reference;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "experience_levels")
public class ExperienceLevel {

    @Id
    private String id;

    private String name;

    public ExperienceLevel() {
    }

    public ExperienceLevel(String name) {
        this.name = name;
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
}
