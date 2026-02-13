package services;

import daos.NeighborhoodDAO;
import daos.OrganizationDAO;
import dtos.OrganizationDTO;
import exceptions.DuplicateResourceException;
import exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import mappers.OrganizationMapper;
import models.Neighborhood;
import models.Organization;
import utils.TransactionHelper;

import java.util.List;

@RequestScoped
public class OrganizationService {

    @Inject
    private OrganizationDAO organizationDAO;

    @Inject
    private NeighborhoodDAO neighborhoodDAO;

    @Inject
    private OrganizationMapper organizationMapper;

    @Inject
    private TransactionHelper txHelper;

    public OrganizationDTO getById(Long id) {
        return organizationMapper.toDTO(validateOrganizationExists(id));
    }

    public List<OrganizationDTO> getAll() {
        return organizationMapper.toDTOList(organizationDAO.findAll());
    }

    public OrganizationDTO create(OrganizationDTO dto) {
        if (dto.getName() != null) {
            Organization existing = organizationDAO.findByName(dto.getName());
            if (existing != null) {
                throw new DuplicateResourceException("Organization named '" + dto.getName() + "' already exists");
            }
        }

        Organization organization = organizationMapper.fromDTO(dto);

        if (dto.getNeighborhoodId() != null) {
            Neighborhood neighborhood = neighborhoodDAO.findById(dto.getNeighborhoodId());
            if (neighborhood == null) {
                throw new ResourceNotFoundException("Neighborhood with ID " + dto.getNeighborhoodId() + " not found");
            }
            organization.setNeighborhood(neighborhood);
        }

        organizationDAO.save(organization);
        return organizationMapper.toDTO(organization);
    }

    public OrganizationDTO update(Long id, OrganizationDTO dto) {
        Organization organization = validateOrganizationExists(id);

        String newName = dto.getName();
        String currentName = organization.getName();
        if (newName != null && !newName.equals(currentName)) {
            Organization existing = organizationDAO.findByName(newName);
            if (existing != null && !existing.getId().equals(id)) {
                throw new DuplicateResourceException("Organization named '" + newName + "' already exists");
            }
        }

        organizationMapper.updateFromDTO(dto, organization);

        if (dto.getNeighborhoodId() != null) {
            Neighborhood neighborhood = neighborhoodDAO.findById(dto.getNeighborhoodId());
            if (neighborhood == null) {
                throw new ResourceNotFoundException("Neighborhood with ID " + dto.getNeighborhoodId() + " not found");
            }
            organization.setNeighborhood(neighborhood);
        }

        organizationDAO.update(organization);
        return organizationMapper.toDTO(organization);
    }

    public void delete(Long id) {
        txHelper.executeInTransaction(() -> {
            Organization o = validateOrganizationExists(id);
            organizationDAO.delete(o);
        });
    }

    private Organization validateOrganizationExists(Long id) {
        Organization o = organizationDAO.findById(id);
        if (o == null) {
            throw new ResourceNotFoundException("Organization not found");
        }
        return o;
    }
}
