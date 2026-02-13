package daos.impl.survey;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.survey.Question;
import daos.survey.QuestionDAO;
import daos.impl.GenericDAOImpl;

import jakarta.persistence.EntityManager;
@RequestScoped
public class QuestionDAOImpl extends GenericDAOImpl<Question> implements QuestionDAO {
    @Inject
    public QuestionDAOImpl(EntityManager em) {
        super(Question.class, em);
    }
    @Override
    public Question findByCode(String code) {
        return em.createQuery("SELECT q FROM Question q WHERE q.code = :code", Question.class)
                .setParameter("code", code)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

}