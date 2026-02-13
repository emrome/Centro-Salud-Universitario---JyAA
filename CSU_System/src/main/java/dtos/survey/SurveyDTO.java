package dtos.survey;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;


@Schema(description = "DTO representing a survey with its metadata")
public class SurveyDTO {
    @Schema(description = "Name of the survey", example = "Encuesta de Satisfacción Ciudadana")
    private String name;
    @Schema(description = "Date when the survey was uploaded", example = "2023-10-01")
    private LocalDate uploadDate;

    public SurveyDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }
}
