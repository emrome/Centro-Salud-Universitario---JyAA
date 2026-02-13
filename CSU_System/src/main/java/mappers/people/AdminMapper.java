package mappers.people;

import dtos.people.AdminCreateDTO;
import dtos.people.AdminDTO;
import jakarta.enterprise.context.ApplicationScoped;
import models.people.Admin;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminMapper {

    public AdminDTO toDTO(Admin entity) {
        if (entity == null) return null;

        AdminDTO dto = new AdminDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setBirthDate(entity.getBirthDate());
        dto.setEmail(entity.getEmail());
        dto.setRegistrationDate(entity.getRegistrationDate());
        dto.setEnabled(entity.isEnabled());
        dto.setPositionInCSU(entity.getPositionInCSU());
        dto.setDeleted(entity.isDeleted());
        return dto;
    }

    public Admin fromCreateDTO(AdminCreateDTO dto) {
        if (dto == null) return null;
        Admin admin = new Admin();
        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());
        admin.setBirthDate(dto.getBirthDate());
        admin.setEmail(dto.getEmail());
        admin.setPassword(dto.getPassword());
        admin.setPositionInCSU(dto.getPositionInCSU());
        admin.setEnabled(dto.isEnabled());
        admin.setDeleted(false);
        admin.setRegistrationDate(dto.getRegistrationDate());
        return admin;
    }

    public void updateFromDTO(AdminDTO dto, Admin entity) {
        if (dto == null || entity == null) return;

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setEmail(dto.getEmail());
        entity.setRegistrationDate(dto.getRegistrationDate());
        entity.setEnabled(dto.isEnabled());
        entity.setPositionInCSU(dto.getPositionInCSU());
        entity.setDeleted(dto.isDeleted());
    }

    public List<AdminDTO> toDTOList(List<Admin> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
