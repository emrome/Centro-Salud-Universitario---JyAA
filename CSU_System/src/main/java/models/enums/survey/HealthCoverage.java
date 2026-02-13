package models.enums.survey;

import models.enums.LabelEnum;

public enum HealthCoverage implements LabelEnum {

    SOCIAL_SECURITY("Obra social/Mutual"),
    PRIVATE_INSURANCE("Prepaga"),
    PAMI_OR_INCLUDE("PAMI/Incluir Salud"),
    PUBLIC_SYSTEM("Sistema Público de Salud"),
    UNKNOWN("No sabe o no contesta");

    private final String label;

    HealthCoverage(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}

