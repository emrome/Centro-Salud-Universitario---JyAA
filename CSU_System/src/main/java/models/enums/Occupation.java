package models.enums;

public enum Occupation implements LabelEnum {
    STUDENT("Estudiante"),
    PROFESSIONAL("Profesional"),
    CSU_STAFF("Personal CSU"),
    VOLUNTEER("Voluntario"),
    OTHER("Otro");

    private final String label;

    Occupation(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}

