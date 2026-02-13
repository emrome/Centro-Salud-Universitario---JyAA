package models.survey;
import models.BaseEntity;

import jakarta.persistence.*;

@Entity
public class Answer extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "question_answer_id", nullable = false)
	private QuestionAnswer questionAnswer;

	private String answer;
	public Answer() {
    	super();
    }

	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public QuestionAnswer getQuestionAnswer() {
		return questionAnswer;
	}
	public void setQuestionAnswer(QuestionAnswer questionAnswer) {
		this.questionAnswer = questionAnswer;
		if (questionAnswer != null && !questionAnswer.getAnswers().contains(this)) {
			questionAnswer.addAnswer(this);
		}
	}
}