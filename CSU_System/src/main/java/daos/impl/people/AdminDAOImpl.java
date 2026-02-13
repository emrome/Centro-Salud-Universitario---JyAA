package daos.impl.people;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.people.Admin;
import daos.people.AdminDAO;

import jakarta.persistence.EntityManager;

@RequestScoped
public class AdminDAOImpl extends UserDAOImpl<Admin> implements AdminDAO {
    @Inject
    public AdminDAOImpl(EntityManager em) {
        super(Admin.class, em);
    }
}