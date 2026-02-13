package dtos.people;

import dtos.GenericDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO representing a social organization representative")
public class SocialOrgRepresentativeDTO extends GenericDTO {

    @Schema(description = "First name", example = "María")
    private String firstName;

    @Schema(description = "Last name", example = "Fernández")
    private String lastName;

    @Schema(description = "Date of birth (YYYY-MM-DD)", example = "1992-07-18")
    private LocalDate birthDate;

    @Schema(description = "Email address", example = "maria.fernandez@csu.org")
    private String email;

    @Schema(description = "Registration date", example = "2025-05-01")
    private LocalDate registrationDate;

    @Schema(description = "Indicates whether the account is enabled", example = "true")
    private boolean enabled;

    @Schema(description = "Organization ID the representative belongs to", example = "12345")
    private Long organizationId;

    @Schema(description = "Name of the organization the representative belongs to", example = "CSU Social Org")
    private String organizationName;

    public SocialOrgRepresentativeDTO() {}

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

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
