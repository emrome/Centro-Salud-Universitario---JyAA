package daos.impl;

import daos.GenericDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class GenericDAOImpl<T> implements GenericDAO<T> {
    protected final Class<T> entityClass;
    protected final EntityManager em;

    public GenericDAOImpl(Class<T> entityClass, EntityManager em) {
        this.entityClass = entityClass;
        this.em = em;
    }

    public void save(T entity) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            em.flush();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    public T findById(Long id) {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.id = :id AND e.isDeleted = false", entityClass)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.isDeleted = false", entityClass).getResultList();
    }

    public void update(T entity) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    public void delete(T entity) {
        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        T attached = em.find(entityClass, id);
        if (attached != null) {
            try {
                entityClass.getMethod("setDeleted", boolean.class)
                        .invoke(attached, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to apply logical delete via reflection", e);
            }
        }
    }
}