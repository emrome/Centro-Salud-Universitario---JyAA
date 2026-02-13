package daos;

import models.Neighborhood;

public interface NeighborhoodDAO extends GenericDAO<Neighborhood> {
    Neighborhood findByName(String name);
}