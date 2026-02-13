package services.people;

import daos.people.HealthStaffDAO;
import dtos.people.HealthStaffCreateDTO;
import dtos.people.HealthStaffDTO;
import exceptions.DuplicateResourceException;
import exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import mappers.people.HealthStaffMapper;
import models.people.HealthStaff;
import utils.PasswordHasher;
import utils.TransactionHelper;

import java.util.List;

@RequestScoped
public class HealthStaffService {

    @Inject
    private HealthStaffDAO healthStaffDAO;

    @Inject
    private HealthStaffMapper healthStaffMapper;

    @Inject
    private PasswordHasher passwordHasher;

    @Inject
    private TransactionHelper txHelper;

    public List<HealthStaffDTO> getAll() {
        return healthStaffMapper.toDTOList(healthStaffDAO.findAll());
    }

    public HealthStaffDTO getById(Long id) {
        return healthStaffMapper.toDTO(validateExists(id));
    }

    public HealthStaffDTO create(HealthStaffCreateDTO dto) {
        if (healthStaffDAO.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("HealthStaff with email '" + dto.getEmail() + "' already exists.");
        }
        if (healthStaffDAO.findByLicense(dto.getLicense()).isPresent()) {
            throw new DuplicateResourceException("HealthStaff with license '" + dto.getLicense() + "' already exists.");
        }

        HealthStaff hs = healthStaffMapper.fromCreateDTO(dto);
        hs.setPassword(passwordHasher.hash(dto.getPassword()));
        healthStaffDAO.save(hs);
        return healthStaffMapper.toDTO(hs);
    }

    public HealthStaffDTO update(Long id, HealthStaffDTO dto) {
        HealthStaff existing = validateExists(id);

        if (!existing.getEmail().equals(dto.getEmail())) {
            healthStaffDAO.findByEmail(dto.getEmail()).ifPresent(h -> {
                if (!h.getId().equals(id)) {
                    throw new DuplicateResourceException("Another HealthStaff with email '" + dto.getEmail() + "' already exists.");
                }
            });
        }

        if (!existing.getLicense().equals(dto.getLicense())) {
            healthStaffDAO.findByLicense(dto.getLicense()).ifPresent(h -> {
                if (!h.getId().equals(id)) {
                    throw new DuplicateResourceException("Another HealthStaff with license '" + dto.getLicense() + "' already exists.");
                }
            });
        }

        healthStaffMapper.updateFromDTO(dto, existing);
        healthStaffDAO.update(existing);
        return healthStaffMapper.toDTO(existing);
    }

    public void delete(Long id) {
        txHelper.executeInTransaction(() -> {
            HealthStaff hs = validateExists(id);
            healthStaffDAO.delete(hs);
        });
    }

    private HealthStaff validateExists(Long id) {
        HealthStaff hs = healthStaffDAO.findById(id);
        if (hs == null) throw new ResourceNotFoundException("HealthStaff not found");
        return hs;
    }
}
