package mappers.people;

import dtos.people.SocialOrgRepresentativeCreateDTO;
import dtos.people.SocialOrgRepresentativeDTO;
import jakarta.enterprise.context.ApplicationScoped;
import models.Organization;
import models.people.SocialOrgRepresentative;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SocialOrgRepresentativeMapper {

    public SocialOrgRepresentativeDTO toDTO(SocialOrgRepresentative entity) {
        if (entity == null) return null;

        SocialOrgRepresentativeDTO dto = new SocialOrgRepresentativeDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setBirthDate(entity.getBirthDate());
        dto.setEmail(entity.getEmail());
        dto.setRegistrationDate(entity.getRegistrationDate());
        dto.setEnabled(entity.isEnabled());
        dto.setDeleted(entity.isDeleted());

        Organization org = entity.getOrganization();
        if (org != null) {
            dto.setOrganizationId(org.getId());          // <-- aplanado del vínculo
            dto.setOrganizationName(org.getName());
        }

        return dto;
    }

    public SocialOrgRepresentative fromCreateDTO(SocialOrgRepresentativeCreateDTO dto) {
        if (dto == null) return null;

        SocialOrgRepresentative entity = new SocialOrgRepresentative();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setRegistrationDate(dto.getRegistrationDate());
        entity.setEnabled(dto.isEnabled());
        entity.setDeleted(false);
        return entity;
    }

    public void updateFromDTO(SocialOrgRepresentativeDTO dto, SocialOrgRepresentative entity) {
        if (dto == null || entity == null) return;

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setEmail(dto.getEmail());
        entity.setRegistrationDate(dto.getRegistrationDate());
        entity.setEnabled(dto.isEnabled());
        entity.setDeleted(dto.isDeleted());
    }

    public List<SocialOrgRepresentativeDTO> toDTOList(List<SocialOrgRepresentative> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
