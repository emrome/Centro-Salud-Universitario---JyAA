package models.enums.survey;

import models.enums.LabelEnum;

public enum EducationLevel implements LabelEnum {
    INITIAL_EDUCATION_4_TO_5("Jardín inicial (4 a 5 años)"),
    PRIMARY_IN_PROGRESS("Primario en curso"),
    PRIMARY_COMPLETE("Primario completo"),
    PRIMARY_INCOMPLETE("Primario incompleto"),
    SECONDARY_IN_PROGRESS("Secundario en curso"),
    SECONDARY_COMPLETE("Secundario completo"),
    SECONDARY_INCOMPLETE("Secundario incompleto"),
    TERTIARY_UNIVERSITY_IN_PROGRESS("Terciario-universitario en curso"),
    TERTIARY_UNIVERSITY_COMPLETE("Terciario-universitario completo"),
    TERTIARY_UNIVERSITY_INCOMPLETE("Terciario-universitario incompleto"),
    TRADE("Oficio"),
    NO_VACANCY("No consigue vacante"),
    NEVER_ATTENDED("Nunca asistió a una institución educativa"),
    NOT_APPLICABLE_UNDER_4("No corresponde (menores de 4 años)");

    private final String label;

    EducationLevel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}