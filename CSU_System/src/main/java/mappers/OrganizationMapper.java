package mappers;

import dtos.CampaignDTO;
import dtos.OrganizationDTO;
import jakarta.enterprise.context.ApplicationScoped;
import models.Campaign;
import models.Organization;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrganizationMapper {

    public OrganizationDTO toDTO(Organization org) {
        if (org == null) return null;

        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(org.getId());
        dto.setName(org.getName());
        dto.setAddress(org.getAddress());
        dto.setDeleted(org.isDeleted());
        dto.setMainActivity(org.getMainActivity());

        if (org.getNeighborhood() != null) {
            dto.setNeighborhoodId(org.getNeighborhood().getId());
            dto.setNeighborhoodName(org.getNeighborhood().getName());
        }
        return dto;
    }

    public Organization fromDTO(OrganizationDTO dto) {
        if (dto == null) return null;

        Organization org = new Organization();
        org.setName(dto.getName());
        org.setAddress(dto.getAddress());
        org.setDeleted(dto.isDeleted());
        org.setMainActivity(dto.getMainActivity());
        return org;
    }

    public void updateFromDTO(OrganizationDTO dto, Organization existing) {
        if (dto == null || existing == null) return;

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getAddress() != null) existing.setAddress(dto.getAddress());
        if (dto.getMainActivity() != null) existing.setMainActivity(dto.getMainActivity());
        existing.setDeleted(dto.isDeleted());
    }

    public List<OrganizationDTO> toDTOList(List<Organization> organizations) {
        if (organizations == null) return null;
        return organizations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
