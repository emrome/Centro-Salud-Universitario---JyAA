package daos.impl.people;

import daos.people.UserDAO;
import jakarta.persistence.EntityManager;
import models.people.User;

import java.util.Optional;

public class UserDAOImpl<T> extends PersonDAOImpl<T> implements UserDAO<T> {

    protected UserDAOImpl(Class<T> entityClass, EntityManager em) {
        super(entityClass, em);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return em.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }
}