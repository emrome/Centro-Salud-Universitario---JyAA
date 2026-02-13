package daos.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.Coordinate;
import models.Zone;
import daos.CoordinateDAO;
import daos.ZoneDAO;

import jakarta.persistence.EntityManager;
import java.util.List;

@RequestScoped
public class ZoneDAOImpl extends GenericDAOImpl<Zone> implements ZoneDAO {
    @Inject
    public ZoneDAOImpl(EntityManager em) {
        super(Zone.class, em);
    }

    @Inject
    private CoordinateDAO coordinateDAO;

    @Override
    public Zone findById(Long id) {
        Zone z = super.findById(id);
        if (z == null) {
            return null;
        }
        z.getCoordinates().removeIf(Coordinate::isDeleted);
        return z;
    }

    @Override
    public List<Zone> findAll() {
        List<Zone> zones = em.createQuery("SELECT z FROM Zone z WHERE z.isDeleted = false", Zone.class).getResultList();
        for (Zone z : zones) {
            if (z.getCoordinates() != null) {
                z.getCoordinates().removeIf(Coordinate::isDeleted);
            }
        }
        return zones;
    }

    @Override
    public void delete(Zone zone) {
        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(zone);
        Zone attached = em.find(Zone.class, id);
        if (attached != null && !attached.isDeleted()) {
            attached.setDeleted(true);
            if (attached.getCoordinates() != null) {
                for (var coordinate : attached.getCoordinates()) {
                    coordinateDAO.delete(coordinate);
                }
            }
        }
    }

    @Override
    public List<Zone> findAllByNeighborhood(Long neighborhoodId) {
        return em.createQuery("""
            SELECT z FROM Zone z
            WHERE z.neighborhood.id = :neighId AND z.isDeleted = false
        """, Zone.class)
        .setParameter("neighId", neighborhoodId)
        .getResultList();
    }

    @Override
    public Zone findByName(String name) {
        return em.createQuery("SELECT z FROM Zone z WHERE z.name = :name AND z.isDeleted = false", Zone.class)
                 .setParameter("name", name)
                 .getResultStream()
                 .findFirst()
                 .orElse(null);
    }
}