package models.enums.survey;

import models.enums.LabelEnum;

public enum HealthCondition implements LabelEnum {
    CARDIOVASCULAR("Cardiovasculares"),
    RESPIRATORY("Respiratorios"),
    SKIN("En la piel"),
    VISION("En la vista"),
    GASTROINTESTINAL("Gastrointestinales"),
    GYNECOLOGICAL("Ginecológicos"),
    ALLERGIES("Alergias"),
    BONES_JOINTS("En los huesos/articulaciones"),
    MENTAL_HEALTH("Salud mental"),
    DIABETES("Diabetes"),
    HYPERTENSION("Hipertensión"),
    THYROID("Problemas de tiroides"),
    DENTAL("Odontológicos"),
    CANCER("Cáncer"),
    STD("Infección de transmisión sexual"),
    NONE("Ninguna"),
    UNKNOWN("No sabe o no contesta");

    private final String label;

    HealthCondition(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
