package daos.impl.survey;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.survey.Answer;
import models.survey.QuestionAnswer;
import daos.survey.AnswerDAO;
import daos.survey.QuestionAnswerDAO;
import daos.impl.GenericDAOImpl;

import jakarta.persistence.EntityManager;

@RequestScoped
public class QuestionAnswerDAOImpl extends GenericDAOImpl<QuestionAnswer> implements QuestionAnswerDAO {
    @Inject
    public QuestionAnswerDAOImpl(EntityManager em) {
        super(QuestionAnswer.class, em);
    }
    @Inject
    private AnswerDAO answerDAO;

    @Override
    public QuestionAnswer findById(Long id) {
        QuestionAnswer questionAnswer = em.find(QuestionAnswer.class, id);
        if (questionAnswer == null) {
            return null;
        }
        questionAnswer.getAnswers().size();
        return questionAnswer;
    }
}