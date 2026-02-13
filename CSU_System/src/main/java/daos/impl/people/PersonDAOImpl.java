package daos.impl.people;

import daos.people.PersonDAO;
import daos.impl.GenericDAOImpl;

import jakarta.persistence.EntityManager;

public class PersonDAOImpl<T> extends GenericDAOImpl<T> implements PersonDAO<T> {
    protected PersonDAOImpl(Class<T> entityClass, EntityManager em) {
        super(entityClass, em);
    }
}