package daos;

import models.Coordinate;

import java.util.List;

public interface CoordinateDAO extends GenericDAO<Coordinate> {
    List<Coordinate> findAllByIds(List<Long> ids);
}