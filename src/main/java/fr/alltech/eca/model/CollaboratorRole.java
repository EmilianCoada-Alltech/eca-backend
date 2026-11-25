
package fr.alltech.eca.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "collaborator_roles")
public class CollaboratorRole {

    @Id
    private String id;

    private String collaboratorId;
    private String roleId;

    public CollaboratorRole() {
    }

    public CollaboratorRole(
            String id,
            String collaboratorId,
            String roleId
    ) {
        this.id = id;
        this.collaboratorId = collaboratorId;
        this.roleId = roleId;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
