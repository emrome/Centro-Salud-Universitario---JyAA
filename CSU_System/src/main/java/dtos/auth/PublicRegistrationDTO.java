package dtos.auth;
import models.enums.Specialty;

import java.time.LocalDate;

public class PublicRegistrationDTO {
    public enum UserType {
        Admin, HealthStaff, SocialOrgRepresentative
    }

    private UserType userType;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String password;
    private String positionInCSU;
    private Specialty specialty;
    private String license;
    private Long organizationId;

    public PublicRegistrationDTO() {}

    public UserType getUserType() {
        return userType;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPositionInCSU() {
        return positionInCSU;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public String getLicense() {
        return license;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setPositionInCSU(String positionInCSU) {
        this.positionInCSU = positionInCSU;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
