package models.survey;

import models.BaseEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import models.enums.survey.SourceType;


@Entity
@Table(name = "QuestionAnswer")
public class QuestionAnswer extends BaseEntity {

    @OneToMany(mappedBy = "questionAnswer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Answer> answers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Column(name = "source_external_id", nullable = false, length = 100)
    private String sourceExternalId;

    @Column(name = "source_owner_external_id", length = 100)
    private String sourceOwnerExternalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 12)
    private SourceType sourceType;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    public QuestionAnswer() {
    	super();
    }

	public List<Answer> getAnswers() {
		return answers;
	}
	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	public void addAnswer(Answer answer) {
		if (this.answers == null) {
			this.answers = new java.util.ArrayList<>();
		}
		this.answers.add(answer);
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public Survey getSurvey() {
		return survey;
	}
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
    public String getSourceExternalId() {
        return sourceExternalId;
    }
    public void setSourceExternalId(String sourceExternalId) {
        this.sourceExternalId = sourceExternalId;
    }
    public String getSourceOwnerExternalId() {
        return sourceOwnerExternalId;
    }
    public void setSourceOwnerExternalId(String sourceOwnerExternalId) {
        this.sourceOwnerExternalId = sourceOwnerExternalId;
    }
    public SourceType getSourceType() {
        return sourceType;
    }
    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }
    public BigDecimal getLatitude() {
        return latitude;
    }
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    public BigDecimal getLongitude() {
        return longitude;
    }
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}