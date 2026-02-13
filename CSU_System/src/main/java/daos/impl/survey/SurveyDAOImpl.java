package daos.impl.survey;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.survey.Answer;
import daos.survey.QuestionAnswerDAO;
import daos.survey.SurveyDAO;
import models.survey.Survey;
import daos.impl.GenericDAOImpl;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class SurveyDAOImpl extends GenericDAOImpl<Survey> implements SurveyDAO {
    @Inject
    public SurveyDAOImpl(EntityManager em) {
        super(Survey.class, em);
    }

    @Override
    public Survey findById(Long id) {
        Survey survey = super.findById(id);
        if (survey == null) {
            return null;
        }
        survey.getQuestionAnswers().forEach(qa -> qa.getAnswers().size());
        return survey;
    }

    @Override
    public void delete(Survey survey)  {
        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(survey);
        Survey attached = em.find(Survey.class, id);
        if (attached != null && !attached.isDeleted()) {
            attached.setDeleted(true);
        }
    }

    @Override
    public Long countAnswersByQuestionCodeAndText(String questionCode, String answerText) {
        return em.createQuery("""
            SELECT COUNT(a)
            FROM QuestionAnswer qa
            JOIN qa.answers a
            JOIN qa.question q
            WHERE q.code = :code
              AND a.answer = :answer
              AND qa.isDeleted = false
              AND a.isDeleted = false
        """, Long.class)
                .setParameter("code", questionCode)
                .setParameter("answer", answerText)
                .getSingleResult();
    }

    @Override
    public Optional<Survey> findByName(String name) {
        return em.createQuery("SELECT s FROM Survey s WHERE s.name = :name", Survey.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Survey> findByQuestionCodeAndAnswerText(String questionCode, String answerText) {
        return em.createQuery("""
            SELECT DISTINCT s
            FROM Survey s
            JOIN s.questionAnswers qa
            JOIN qa.answers a
            JOIN qa.question q
            WHERE q.code = :code
              AND a.answer = :answer
              AND qa.isDeleted = false
              AND a.isDeleted = false
        """, Survey.class)
                .setParameter("code", questionCode)
                .setParameter("answer", answerText)
                .getResultList();
    }

    @Override
    public List<Survey> findByAgeRange(int minAge, int maxAge) {
        return getSurveysInAgeRange(minAge, maxAge);
    }

    @Override
    public Long countByAgeRange(int minAge, int maxAge) {
        return (long) getSurveysInAgeRange(minAge, maxAge).size();
    }

    private List<Survey> getSurveysInAgeRange(int minAge, int maxAge) {
        try {
            List<Survey> all = em.createQuery("""
            SELECT DISTINCT s
            FROM Survey s
            JOIN s.questionAnswers qa
            JOIN qa.question q
            JOIN qa.answers a
            WHERE q.code = :ageCode AND qa.isDeleted = false AND a.isDeleted = false
        """, Survey.class)
                    .setParameter("ageCode", "3")
                    .getResultList();

            return all.stream()
                    .filter(survey -> survey.getQuestionAnswers().stream()
                            .filter(qa -> qa.getQuestion().getCode().equals("3"))
                            .flatMap(qa -> qa.getAnswers().stream())
                            .map(Answer::getAnswer)
                            .map(answer -> {
                                try {
                                    return Integer.parseInt(answer);
                                } catch (NumberFormatException e) {
                                    return null;
                                }
                            })
                            .anyMatch(age -> age != null && age >= minAge && age <= maxAge)
                    )
                    .toList();
        }
        catch (Exception e) {
            return List.of();
        }
    }
}