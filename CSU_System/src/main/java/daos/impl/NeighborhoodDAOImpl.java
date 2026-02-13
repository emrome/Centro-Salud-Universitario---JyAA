package daos.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.Coordinate;
import models.Neighborhood;
import daos.CoordinateDAO;
import daos.NeighborhoodDAO;
import daos.ZoneDAO;
import jakarta.persistence.EntityManager;

import java.util.List;

@RequestScoped
public class NeighborhoodDAOImpl extends GenericDAOImpl<Neighborhood> implements NeighborhoodDAO {
    @Inject
    private ZoneDAO zoneDAO;

    @Inject
    private CoordinateDAO coordinateDAO;

    @Inject
    public NeighborhoodDAOImpl(EntityManager em) {
        super(Neighborhood.class, em);
    }

    @Override
    public Neighborhood findById(Long id) {
        Neighborhood n = super.findById(id);
        if (n == null) {
            return null;
        }
        n.getGeolocation().removeIf(Coordinate::isDeleted);
        return n;
    }

    @Override
    public List<Neighborhood> findAll() {
        List<Neighborhood> list = super.findAll();
        for (Neighborhood n : list) {
            if (n.getGeolocation() != null) {
                n.getGeolocation().removeIf(Coordinate::isDeleted);
            }
        }
        return list;
    }


    @Override
    public void delete(Neighborhood neighborhood) {
        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(neighborhood);
        Neighborhood attached = em.find(Neighborhood.class, id);
        if (attached != null && !attached.isDeleted()) {
            attached.setDeleted(true);
            if (attached.getZones() != null) {
                for (var zone : attached.getZones()) {
                    zoneDAO.delete(zone);
                }
            }

            if (attached.getGeolocation() != null) {
                for (var coordinate : attached.getGeolocation()) {
                    if (!coordinate.isDeleted()) {
                        coordinateDAO.delete(coordinate);
                    }
                }
            }
        }
    }

    @Override
    public Neighborhood findByName(String name) {
        return em.createQuery("SELECT n FROM Neighborhood n WHERE n.name = :name AND n.isDeleted = false", Neighborhood.class)
                 .setParameter("name", name)
                 .getResultStream()
                 .findFirst()
                 .orElse(null);

    }

}