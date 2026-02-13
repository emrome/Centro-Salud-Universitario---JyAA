package daos.impl.people;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.people.Surveyor;
import daos.people.SurveyorDAO;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class SurveyorDAOImpl extends PersonDAOImpl<Surveyor> implements SurveyorDAO {
    @Inject
    public SurveyorDAOImpl(EntityManager em) {
        super(Surveyor.class, em);
    }

    @Override
    public Optional<Surveyor> findByDni(String dni) {
        return em.createQuery("SELECT s FROM Surveyor s WHERE s.dni = :dni", Surveyor.class)
                 .setParameter("dni", dni).getResultStream().findFirst();
    }

    @Override
    public List<Surveyor> findAllByEvent(Long eventId) {
        return em.createQuery("""
        SELECT s FROM Event e
        JOIN e.surveyors s
        WHERE e.id = :eventId AND s.isDeleted = false
        """, Surveyor.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }
}