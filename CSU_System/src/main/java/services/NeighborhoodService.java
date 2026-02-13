package services;

import dtos.*;
import exceptions.DuplicateResourceException;
import exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import mappers.NeighborhoodMapper;
import models.Neighborhood;
import daos.NeighborhoodDAO;
import utils.TransactionHelper;

import java.util.List;

@RequestScoped
public class NeighborhoodService {
    @Inject
    private NeighborhoodDAO neighborhoodDAO;
    @Inject
    TransactionHelper txHelper;
    @Inject
    private NeighborhoodMapper neighborhoodMapper;

    public NeighborhoodDTO getById(Long id) {
        return neighborhoodMapper.toDTO(validateNeighborhoodExists(id));
    }

    public List<NeighborhoodDTO> getAll() {
        return neighborhoodMapper.toDTOList(neighborhoodDAO.findAll());
    }

    public NeighborhoodDTO create(NeighborhoodDTO dto) {
        if (neighborhoodDAO.findByName(dto.getName()) != null) {
            throw new DuplicateResourceException("Neighborhood named '" + dto.getName() + "' already exists");
        }
        Neighborhood n = neighborhoodMapper.fromDTO(dto);
        neighborhoodDAO.save(n);
        return neighborhoodMapper.toDTO(n);
    }

    public NeighborhoodDTO update(Long id, NeighborhoodDTO dto) {
        Neighborhood neighborhood = validateNeighborhoodExists(id);

        String newName = dto.getName();
        String currentName = neighborhood.getName();

        if (newName != null && !newName.equals(currentName)) {
            Neighborhood existing = neighborhoodDAO.findByName(newName);
            if (existing != null && !existing.getId().equals(id)) {
                throw new DuplicateResourceException("Neighborhood named '" + newName + "' already exists");
            }
        }

        neighborhoodMapper.updateFromDTO(dto, neighborhood);
        neighborhoodDAO.update(neighborhood);
        return neighborhoodMapper.toDTO(neighborhood);
    }

    public void delete(Long id) {
        txHelper.executeInTransaction(() -> {
            Neighborhood n = validateNeighborhoodExists(id);
            neighborhoodDAO.delete(n);
        });
    }

    private Neighborhood validateNeighborhoodExists(Long id) {
        Neighborhood n = neighborhoodDAO.findById(id);
        if (n == null) throw new ResourceNotFoundException("Neighborhood not found");
        return n;
    }
}

