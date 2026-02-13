package models.enums.survey;

import models.enums.LabelEnum;

public enum MedicationAccess implements LabelEnum {
    REGULAR_ACCESS("Sí, regularmente"),
    IRREGULAR_ACCESS("Sí, pero no regularmente"),
    NO_ACCESS("No puede acceder"),
    UNKNOWN("No sabe o no contesta");

    private final String label;

    MedicationAccess(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}