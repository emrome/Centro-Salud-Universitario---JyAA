package services.people;

import daos.people.AdminDAO;
import dtos.people.AdminCreateDTO;
import dtos.people.AdminDTO;
import exceptions.DuplicateResourceException;
import exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import mappers.people.AdminMapper;
import models.people.Admin;
import utils.PasswordHasher;
import utils.TransactionHelper;

import java.util.List;

@RequestScoped
public class AdminService {

    @Inject
    private AdminDAO adminDAO;

    @Inject
    private AdminMapper adminMapper;

    @Inject
    private PasswordHasher passwordHasher;

    @Inject
    private TransactionHelper txHelper;

    public List<AdminDTO> getAll() {
        return adminMapper.toDTOList(adminDAO.findAll());
    }

    public AdminDTO getById(Long id) {
        return adminMapper.toDTO(validateExists(id));
    }

    public AdminDTO create(AdminCreateDTO dto) {
        if (adminDAO.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Admin with email '" + dto.getEmail() + "' already exists.");
        }

        Admin admin = adminMapper.fromCreateDTO(dto);
        admin.setPassword(passwordHasher.hash(dto.getPassword()));
        adminDAO.save(admin);
        return adminMapper.toDTO(admin);
    }

    public AdminDTO update(Long id, AdminDTO dto) {
        Admin existing = validateExists(id);

        if (!existing.getEmail().equals(dto.getEmail())) {
            adminDAO.findByEmail(dto.getEmail()).ifPresent(a -> {
                if (!a.getId().equals(id)) {
                    throw new DuplicateResourceException("Another admin with email '" + dto.getEmail() + "' already exists.");
                }
            });
        }

        adminMapper.updateFromDTO(dto, existing);
        adminDAO.update(existing);
        return adminMapper.toDTO(existing);
    }

    public void delete(Long id) {
        txHelper.executeInTransaction(() -> {
            Admin admin = validateExists(id);
            adminDAO.delete(admin);
        });
    }

    private Admin validateExists(Long id) {
        Admin admin = adminDAO.findById(id);
        if (admin == null) throw new ResourceNotFoundException("Admin not found");
        return admin;
    }
}
