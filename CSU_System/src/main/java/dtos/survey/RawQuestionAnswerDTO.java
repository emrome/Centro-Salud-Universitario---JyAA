package dtos.survey;

import java.util.List;

public class RawQuestionAnswerDTO {
    private String code;
    private String label;
    private List<String> values;

    public RawQuestionAnswerDTO(String code, String label, List<String> values) {
        this.code = code;
        this.label = label;
        this.values = values;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
    public List<String> getValues() { return values; }
}

