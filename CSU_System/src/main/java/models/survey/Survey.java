package models.survey;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import models.BaseEntity;

import jakarta.persistence.*;

@Entity
public class Survey extends BaseEntity {

	@Column(unique = true, nullable = false)
	private String name;

	@Column(nullable = false)
    private LocalDate uploadDate;

	@OneToMany(mappedBy = "survey",  cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<QuestionAnswer> questionAnswers;

    public Survey() {
		super();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(LocalDate uploadDate) {
		this.uploadDate = uploadDate;
	}
	public List<QuestionAnswer> getQuestionAnswers() {
		return questionAnswers;
	}
	public void setQuestionAnswers(List<QuestionAnswer> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}
	public void addQuestionAnswer(QuestionAnswer questionAnswer) {
		this.questionAnswers.add(questionAnswer);
		questionAnswer.setSurvey(this);
	}
}
