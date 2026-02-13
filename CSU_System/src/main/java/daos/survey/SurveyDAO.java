package daos.survey;

import models.survey.Survey;
import daos.GenericDAO;
import java.util.List;
import java.util.Optional;

public interface SurveyDAO extends GenericDAO<Survey> {
    Optional<Survey> findByName(String name);
    Long countAnswersByQuestionCodeAndText(String questionCode, String answerText);
    List<Survey> findByQuestionCodeAndAnswerText(String questionCode, String answerText);
    List<Survey> findByAgeRange( int minAge, int maxAge);
    Long countByAgeRange( int minAge, int maxAge);
}