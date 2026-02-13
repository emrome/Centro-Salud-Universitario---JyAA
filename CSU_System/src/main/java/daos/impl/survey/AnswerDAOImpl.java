package daos.impl.survey;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.survey.Answer;
import daos.impl.GenericDAOImpl;
import daos.survey.AnswerDAO;

import jakarta.persistence.EntityManager;

@RequestScoped
public class AnswerDAOImpl extends GenericDAOImpl<Answer> implements AnswerDAO {
    @Inject
    public AnswerDAOImpl(EntityManager em) {
        super(Answer.class, em);
    }
}