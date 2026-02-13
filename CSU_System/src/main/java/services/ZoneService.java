package services;

import daos.ZoneDAO;
import daos.NeighborhoodDAO;
import dtos.ZoneDTO;
import exceptions.DuplicateResourceException;
import exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import mappers.ZoneMapper;
import models.Zone;
import models.Neighborhood;

import utils.TransactionHelper;
import java.util.List;

@RequestScoped
public class ZoneService {

    @Inject
    private ZoneDAO zoneDAO;
    @Inject
    private NeighborhoodDAO neighborhoodDAO;
    @Inject
    private TransactionHelper txHelper;
    @Inject
    private ZoneMapper zoneMapper;

    public List<ZoneDTO> getZonesByNeighborhood(Long neighborhoodId) {
        validateNeighborhoodExists(neighborhoodId);
        return zoneMapper.toDTOList(zoneDAO.findAllByNeighborhood(neighborhoodId));
    }

    public ZoneDTO getZoneInNeighborhood(Long neighborhoodId, Long zoneId) {
        Zone zone = validateZoneInNeighborhood(neighborhoodId, zoneId);
        return zoneMapper.toDTO(zone);
    }

    public ZoneDTO createInNeighborhood(Long neighborhoodId, ZoneDTO dto) {
        Neighborhood neighborhood = validateNeighborhoodExists(neighborhoodId);
        if (zoneDAO.findByName(dto.getName()) != null) {
            throw new DuplicateResourceException("Zone with name '" + dto.getName() + "' already exists");
        }
        Zone zone = zoneMapper.fromDTO(dto);
        zone.setNeighborhood(neighborhood);
        zoneDAO.save(zone);
        return zoneMapper.toDTO(zone);
    }

    public ZoneDTO updateInNeighborhood(Long neighborhoodId, Long zoneId, ZoneDTO dto) {
        Zone zone = validateZoneInNeighborhood(neighborhoodId, zoneId);
        String newName = dto.getName();
        if (newName != null && !newName.equals(zone.getName())) {
            Zone existing = zoneDAO.findByName(newName);
            if (existing != null && !existing.getId().equals(zone.getId())) {
                throw new DuplicateResourceException("Zone named '" + newName + "' already exists");
            }
        }
        zoneMapper.updateFromDTO(dto, zone);
        zoneDAO.update(zone);
        return zoneMapper.toDTO(zone);
    }

    public void deleteInNeighborhood(Long neighborhoodId, Long zoneId) {
        txHelper.executeInTransaction(() -> {
            Zone zone = validateZoneInNeighborhood(neighborhoodId, zoneId);
            zoneDAO.delete(zone);
        });
    }

    private Neighborhood validateNeighborhoodExists(Long neighborhoodId) {
        Neighborhood n = neighborhoodDAO.findById(neighborhoodId);
        if (n == null) throw new ResourceNotFoundException("Neighborhood not found");
        return n;
    }

    private Zone validateZoneInNeighborhood(Long neighborhoodId, Long zoneId) {
        Zone z = zoneDAO.findById(zoneId);
        if (z == null || z.getNeighborhood() == null || !z.getNeighborhood().getId().equals(neighborhoodId)) {
            throw new ResourceNotFoundException("Zone not found in neighborhood");
        }
        return z;
    }
}