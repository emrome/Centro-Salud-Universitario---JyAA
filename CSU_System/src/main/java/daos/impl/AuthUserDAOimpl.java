package daos.impl;

import daos.AuthUserDAO;
import daos.impl.people.UserDAOImpl;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import models.people.User;

@RequestScoped
public class AuthUserDAOimpl extends UserDAOImpl<User> implements AuthUserDAO {
    @Inject
    public AuthUserDAOimpl(EntityManager em) {
        super(User.class, em);
    }
}
