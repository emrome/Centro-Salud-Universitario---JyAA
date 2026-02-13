package models.enums;

public enum MainActivity implements LabelEnum{
    HEALTH("Salud"),
    EDUCATION("Educación"),
    SPORTS("Deportes"),
    CULTURE("Cultura"),
    SOCIAL_ASSISTANCE("Asistencia social");

    private final String label;

    MainActivity(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}