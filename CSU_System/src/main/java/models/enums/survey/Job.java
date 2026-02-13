package models.enums.survey;

import models.enums.LabelEnum;

public enum Job implements LabelEnum {

    MANUFACTURING_INDUSTRY("Industria manufacturera (fábrica)"),
    CONSTRUCTION("Construcción"),
    COMMERCE("Comercio"),
    TRANSPORT("Transporte (colectivo, taxi, remis, camión)"),
    HEALTH("Salud"),
    PUBLIC_EMPLOYMENT("Empleo público (municipio, provincia, nación)"),
    SECURITY_FORCES("Fuerza de seguridad - seguridad privada - servicio penitenciario"),
    PRIVATE_EMPLOYMENT("Empleo privado"),
    DOMESTIC_SERVICE("Servicio doméstico - cuidado de personas"),
    COMMUNITY_SERVICES("Servicios comunitarios - cooperativas - reciclado"),
    INFORMAL_WORK("Changa - venta ambulante"),
    SELF_EMPLOYED("Cuentapropista"),
    OTHER("Otros"),
    DONT_KNOW_OR_NO_ANSWER("No sabe o no contesta");

    private final String label;

    Job(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
