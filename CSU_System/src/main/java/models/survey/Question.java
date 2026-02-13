package models.survey;
import models.BaseEntity;

import jakarta.persistence.*;

@Entity
public class Question extends BaseEntity {
	private String question;

	private String code;

    public Question() {
    	super();
    }

	public String getText() {
		return question;
	}
	public void setText(String question) {
		this.question = question;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}