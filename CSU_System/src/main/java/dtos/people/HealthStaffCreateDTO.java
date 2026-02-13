package dtos.people;

import io.swagger.v3.oas.annotations.media.Schema;
import models.enums.Specialty;

import java.time.LocalDate;
@Schema(description = "DTO used when creating a Health Staff member")
public class HealthStaffCreateDTO {

    @Schema(description = "First name of the health staff member", example = "Alice")
    private String firstName;

    @Schema(description = "Last name of the health staff member", example = "Johnson")
    private String lastName;

    @Schema(description = "Date of birth (YYYY-MM-DD)", example = "1990-08-15")
    private LocalDate birthDate;

    @Schema(description = "Email address", example = "alicej@csu.org")
    private String email;

    @Schema(description = "Plain password to hash and store", example = "securePassword123")
    private String password;

    @Schema(description = "License number", example = "LIC123456")
    private String license;

    @Schema(description = "Specialty of the health staff member", example = "CARDIOLOGY")
    private Specialty specialty;

    @Schema(description = "Registration date", example = "2024-10-01")
    private LocalDate registrationDate;

    @Schema(description = "Indicates whether the account is enabled", example = "true")
    private boolean enabled;

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

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
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
}
