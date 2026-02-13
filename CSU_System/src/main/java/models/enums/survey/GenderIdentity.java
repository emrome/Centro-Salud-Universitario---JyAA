package models.enums.survey;

import models.enums.LabelEnum;

public enum GenderIdentity implements LabelEnum {

    WOMAN_CIS("Mujer cis"),
    WOMAN_TRANS("Mujer trans / travesti"),
    MAN_CIS("Varón cis"),
    MAN_TRANS("Varón trans / masculinidad trans"),
    NON_BINARY("No binarie"),
    OTHER_IDENTITY("Otra identidad / ninguna de las anteriores"),
    DONT_KNOW_OR_NO_ANSWER("No sabe o no contesta");

    private final String label;

    GenderIdentity(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
