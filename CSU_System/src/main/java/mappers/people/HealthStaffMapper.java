package mappers.people;

import dtos.people.HealthStaffCreateDTO;
import dtos.people.HealthStaffDTO;
import jakarta.enterprise.context.ApplicationScoped;
import models.people.HealthStaff;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class HealthStaffMapper {

    public HealthStaffDTO toDTO(HealthStaff entity) {
        if (entity == null) return null;

        HealthStaffDTO dto = new HealthStaffDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setBirthDate(entity.getBirthDate());
        dto.setEmail(entity.getEmail());
        dto.setRegistrationDate(entity.getRegistrationDate());
        dto.setEnabled(entity.isEnabled());
        dto.setSpecialty(entity.getSpecialty());
        dto.setLicense(entity.getLicense());
        dto.setDeleted(entity.isDeleted());
        return dto;
    }

    public HealthStaff fromCreateDTO(HealthStaffCreateDTO dto) {
        if (dto == null) return null;
        HealthStaff hs = new HealthStaff();
        hs.setFirstName(dto.getFirstName());
        hs.setLastName(dto.getLastName());
        hs.setBirthDate(dto.getBirthDate());
        hs.setEmail(dto.getEmail());
        hs.setPassword(dto.getPassword());
        hs.setLicense(dto.getLicense());
        hs.setSpecialty(dto.getSpecialty());
        hs.setRegistrationDate(dto.getRegistrationDate());
        hs.setEnabled(dto.isEnabled());
        hs.setDeleted(false);
        return hs;
    }

    public void updateFromDTO(HealthStaffDTO dto, HealthStaff entity) {
        if (dto == null || entity == null) return;

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setEmail(dto.getEmail());
        entity.setRegistrationDate(dto.getRegistrationDate());
        entity.setEnabled(dto.isEnabled());
        entity.setSpecialty(dto.getSpecialty());
        entity.setLicense(dto.getLicense());
        entity.setDeleted(dto.isDeleted());
    }

    public List<HealthStaffDTO> toDTOList(List<HealthStaff> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
