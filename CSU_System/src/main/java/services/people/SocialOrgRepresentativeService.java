package services.people;

import daos.OrganizationDAO;
import daos.people.SocialOrgRepresentativeDAO;
import dtos.people.SocialOrgRepresentativeCreateDTO;
import dtos.people.SocialOrgRepresentativeDTO;
import exceptions.DuplicateResourceException;
import exceptions.InvalidDataException;
import exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import mappers.people.SocialOrgRepresentativeMapper;
import models.people.SocialOrgRepresentative;
import models.Organization;
import utils.PasswordHasher;
import utils.TransactionHelper;

import java.util.List;

@RequestScoped
public class SocialOrgRepresentativeService {

    @Inject
    private SocialOrgRepresentativeDAO representativeDAO;

    @Inject
    private SocialOrgRepresentativeMapper representativeMapper;

    @Inject
    private PasswordHasher passwordHasher;

    @Inject
    private TransactionHelper txHelper;

    @Inject
    private OrganizationDAO organizationDAO;

    public List<SocialOrgRepresentativeDTO> getAll() {
        return representativeMapper.toDTOList(representativeDAO.findAll());
    }

    public SocialOrgRepresentativeDTO getById(Long id) {
        return representativeMapper.toDTO(validateExists(id));
    }

    public SocialOrgRepresentativeDTO create(SocialOrgRepresentativeCreateDTO dto) {
        if (representativeDAO.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("A representative with email '" + dto.getEmail() + "' already exists.");
        }

        if (dto.getOrganizationId() == null) {
            throw new InvalidDataException("organizationId is required");
        }
        Organization org = organizationDAO.findById(dto.getOrganizationId());
        if (org == null) {
            throw new ResourceNotFoundException("Organization with ID " + dto.getOrganizationId() + " not found");
        }

        SocialOrgRepresentative representative = representativeMapper.fromCreateDTO(dto);
        representative.setOrganization(org);
        representative.setPassword(passwordHasher.hash(dto.getPassword()));
        representativeDAO.save(representative);
        return representativeMapper.toDTO(representative);
    }

    public List<SocialOrgRepresentativeDTO> getByOrganization(Long orgId) {
        return representativeMapper.toDTOList(representativeDAO.findByOrganizationId(orgId));
    }

    public SocialOrgRepresentativeDTO update(Long id, SocialOrgRepresentativeDTO dto) {
        SocialOrgRepresentative existing = validateExists(id);

        if (!existing.getEmail().equals(dto.getEmail())) {
            representativeDAO.findByEmail(dto.getEmail()).ifPresent(r -> {
                if (!r.getId().equals(id)) {
                    throw new DuplicateResourceException("Another representative with email '" + dto.getEmail() + "' already exists.");
                }
            });
        }

        representativeMapper.updateFromDTO(dto, existing);
        if (dto.getOrganizationId() != null) {
            Organization org = organizationDAO.findById(dto.getOrganizationId());
            if (org == null) {
                throw new ResourceNotFoundException("Organization with ID " + dto.getOrganizationId() + " not found");
            }
            existing.setOrganization(org);
        }
        representativeDAO.update(existing);
        return representativeMapper.toDTO(existing);
    }

    public void delete(Long id) {
        txHelper.executeInTransaction(() -> {
            SocialOrgRepresentative r = validateExists(id);
            representativeDAO.delete(r);
        });
    }

    private SocialOrgRepresentative validateExists(Long id) {
        SocialOrgRepresentative r = representativeDAO.findById(id);
        if (r == null) throw new ResourceNotFoundException("Representative not found");
        return r;
    }
}
