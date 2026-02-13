package dtos.survey;

import models.enums.survey.SourceType;
import java.util.ArrayList;
import java.util.List;

public class RawPersonResponseDTO {

    private List<RawQuestionAnswerDTO> answers;
    private SourceType sourceType;
    private String sourceExternalId;
    private String sourceOwnerExternalId;

    private Double latitude;
    private Double longitude;

    public RawPersonResponseDTO() {
        this.answers = new ArrayList<>();
    }

    public RawPersonResponseDTO(List<RawQuestionAnswerDTO> answers) {
        this.answers = answers;
    }

    public List<RawQuestionAnswerDTO> getAnswers() { return answers; }
    public void setAnswers(List<RawQuestionAnswerDTO> answers) { this.answers = answers; }
    public void addAnswer(RawQuestionAnswerDTO answer) { this.answers.add(answer); }

    public SourceType getSourceType() { return sourceType; }
    public void setSourceType(SourceType sourceType) { this.sourceType = sourceType; }

    public String getSourceExternalId() { return sourceExternalId; }
    public void setSourceExternalId(String sourceExternalId) { this.sourceExternalId = sourceExternalId; }

    public String getSourceOwnerExternalId() { return sourceOwnerExternalId; }
    public void setSourceOwnerExternalId(String sourceOwnerExternalId) { this.sourceOwnerExternalId = sourceOwnerExternalId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
