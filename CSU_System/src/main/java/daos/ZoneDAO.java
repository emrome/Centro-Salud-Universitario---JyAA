package daos;

import models.Zone;
import java.util.List;

public interface ZoneDAO extends GenericDAO<Zone> {
    List<Zone> findAllByNeighborhood(Long neighborhoodId);
    Zone findByName(String name);
}