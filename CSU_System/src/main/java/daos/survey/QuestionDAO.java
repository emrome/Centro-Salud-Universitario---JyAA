package daos.survey;

import models.survey.Question;
import daos.GenericDAO;

public interface QuestionDAO extends GenericDAO<Question> {
    Question findByCode(String code);
}