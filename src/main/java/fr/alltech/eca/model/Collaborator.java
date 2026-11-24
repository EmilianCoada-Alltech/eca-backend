
package fr.alltech.eca.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "collaborators")
public class Collaborator {

    @Id
    private String id;

    private String adIdentifier;
    private String firstName;
    private String lastName;
    private String email;

    private String agencyId;
    private String professionId;
    private String experienceLevelId;

    private LocalDate hireDate;

    private String biography;
    private String photoUrl;

    private String profileStatus;
    private boolean onboardingCompleted;
    private boolean searchable;


    public Collaborator() {
    }

    public Collaborator(
            String adIdentifier,
            String firstName,
            String lastName,
            String email,
            String agencyId
    ) {
        this.adIdentifier = adIdentifier;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.agencyId = agencyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdIdentifier() {
        return adIdentifier;
    }

    public void setAdIdentifier(String adIdentifier) {
        this.adIdentifier = adIdentifier;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getProfessionId() {
        return professionId;
    }

    public void setProfessionId(String professionId) {
        this.professionId = professionId;
    }

    public String getExperienceLevelId() {
        return experienceLevelId;
    }

    public void setExperienceLevelId(String experienceLevelId) {
        this.experienceLevelId = experienceLevelId;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(String profileStatus) {
        this.profileStatus = profileStatus;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

}
