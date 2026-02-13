package daos.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.Event;
import daos.EventDAO;

import jakarta.persistence.EntityManager;
@RequestScoped
public class EventDAOImpl extends GenericDAOImpl<Event> implements EventDAO {
    @Inject
    public EventDAOImpl(EntityManager em) {
        super(Event.class, em);
    }

    @Override
    public Event findById(Long id) {
        Event event = super.findById(id);
        if (event == null) {
            return null;
        }
        event.getSurveyors().size();
        return event;
    }
}