package daos.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import models.Coordinate;
import daos.CoordinateDAO;

import jakarta.persistence.EntityManager;

import java.util.List;

@RequestScoped
public class CoordinateDAOImpl extends GenericDAOImpl<Coordinate> implements CoordinateDAO {
    @Inject
    public CoordinateDAOImpl(EntityManager em) {
        super(Coordinate.class, em);
    }

    @Override
    public List<Coordinate> findAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        TypedQuery<Coordinate> query = em.createQuery(
                "SELECT c FROM Coordinate c WHERE c.id IN :ids AND c.isDeleted = false", Coordinate.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }
}