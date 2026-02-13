package daos.impl.people;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.people.HealthStaff;
import daos.people.HealthStaffDAO;

import jakarta.persistence.EntityManager;

import java.util.Optional;

@RequestScoped
public class HealthStaffDAOImpl extends UserDAOImpl<HealthStaff> implements HealthStaffDAO {

    @Inject
    public HealthStaffDAOImpl(EntityManager em) {
        super(HealthStaff.class, em);
    }

    @Override
    public Optional<HealthStaff> findByLicense(String license) {
        return em.createQuery("SELECT h FROM HealthStaff h WHERE h.license = :license", HealthStaff.class)
                .setParameter("license", license)
                .getResultStream()
                .findFirst();
    }

}