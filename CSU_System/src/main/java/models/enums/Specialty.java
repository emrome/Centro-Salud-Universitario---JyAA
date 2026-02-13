package models.enums;

public enum Specialty implements LabelEnum {
    CLINIC("Clínica"),
    NURSING("Enfermería"),
    PSYCHOLOGY("Psicología"),
    NUTRITION("Nutrición"),
    OBSTETRICS("Obstetricia");

    private final String label;

    Specialty(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
