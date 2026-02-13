package dtos.people;

import io.swagger.v3.oas.annotations.media.Schema;
import models.enums.survey.GenderIdentity;
import models.enums.Occupation;

import java.time.LocalDate;

@Schema(description = "DTO representing a surveyor (encuestador)")

public class SurveyorDTO {
    @Schema(description = "Unique identifier of the surveyor", example = "1")
    private Long id;

    @Schema(description = "First name of the surveyor", example = "Juan Pedro")
    private String firstName;

    @Schema(description = "Last name of the surveyor", example = "Torres")
    private String lastName;

    @Schema(description = "Date of birth (YYYY-MM-DD)", example = "1992-05-15")
    private LocalDate birthDate;

    @Schema(description = "DNI (national ID)", example = "34899123")
    private String dni;

    @Schema(description = "Gender identity", example = "MAN_CIS")
    private GenderIdentity gender;

    @Schema(description = "Occupation of the surveyor", example = "VOLUNTEER")
    private Occupation occupation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }

    public GenderIdentity getGender() {
        return gender;
    }
    public void setGender(GenderIdentity gender) {
        this.gender = gender;
    }

    public Occupation getOccupation() {
        return occupation;
    }
    public void setOccupation(Occupation occupation) {
        this.occupation = occupation;
    }
}
