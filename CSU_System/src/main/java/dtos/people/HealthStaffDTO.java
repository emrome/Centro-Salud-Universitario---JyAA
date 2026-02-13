package dtos.people;

import dtos.GenericDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import models.enums.Specialty;

import java.time.LocalDate;

@Schema(description = "DTO representing a health staff user")
public class HealthStaffDTO extends GenericDTO {

    @Schema(description = "First name", example = "María")
    private String firstName;

    @Schema(description = "Last name", example = "Gómez")
    private String lastName;

    @Schema(description = "Date of birth (YYYY-MM-DD)", example = "1985-03-22")
    private LocalDate birthDate;

    @Schema(description = "Email address", example = "maria.gomez@csu.org")
    private String email;

    @Schema(description = "Registration date", example = "2024-09-15")
    private LocalDate registrationDate;

    @Schema(description = "Indicates whether the account is enabled", example = "true")
    private boolean enabled;

    @Schema(description = "Professional specialty", example = "NUTRITION")
    private Specialty specialty;

    @Schema(description = "Professional license number", example = "MAT-4567-LP")
    private String license;

    public HealthStaffDTO() {}

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

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
