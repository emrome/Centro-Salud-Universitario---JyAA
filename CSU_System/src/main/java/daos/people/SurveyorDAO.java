package daos.people;

import models.people.Surveyor;

import java.util.List;
import java.util.Optional;

public interface SurveyorDAO extends PersonDAO<Surveyor> {
    Optional<Surveyor> findByDni(String dni);
    List<Surveyor> findAllByEvent(Long eventId);
}