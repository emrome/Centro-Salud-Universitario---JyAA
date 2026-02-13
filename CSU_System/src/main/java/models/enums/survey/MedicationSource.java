package models.enums.survey;

import models.enums.LabelEnum;
public enum MedicationSource implements LabelEnum {
    PUBLIC_HEALTH_CENTER("Centro de salud o salita pública"),
    PUBLIC_HOSPITAL("Hospital público"),
    HEALTH_CAMPAIGN("Posta/operativo de salud"),
    SOCIAL_SECURITY("Obra social"),
    PURCHASED("Lo compra"),
    UNKNOWN("No sabe o no contesta");

    private final String label;

    MedicationSource(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}