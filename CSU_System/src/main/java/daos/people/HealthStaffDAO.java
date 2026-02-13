package daos.people;

import models.people.HealthStaff;

import java.util.Optional;

public interface HealthStaffDAO extends UserDAO<HealthStaff> {
    Optional<HealthStaff> findByLicense(String license);
}