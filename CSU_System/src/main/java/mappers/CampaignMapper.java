package mappers;

import dtos.CampaignDTO;
import dtos.survey.SurveyDTO;
import jakarta.enterprise.context.ApplicationScoped;
import models.Campaign;
import models.Neighborhood;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CampaignMapper {

    public CampaignDTO toDTO(Campaign entity) {
        if (entity == null) return null;

        CampaignDTO dto = new CampaignDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setDeleted(entity.isDeleted());

        if (entity.getNeighborhood() != null) {
            dto.setNeighborhoodId(entity.getNeighborhood().getId());
        }

        if (entity.getSurvey() != null) {
            SurveyDTO surveyDTO = new SurveyDTO();
            surveyDTO.setName(entity.getSurvey().getName());
            surveyDTO.setUploadDate(entity.getSurvey().getUploadDate());
            dto.setSurvey(surveyDTO);
        }

        return dto;
    }

    public Campaign fromDTO(CampaignDTO dto) {
        if (dto == null) return null;

        Campaign entity = new Campaign();
        entity.setName(dto.getName());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setDeleted(dto.isDeleted());

        if (dto.getNeighborhoodId() != null) {
            Neighborhood n = new Neighborhood();
            n.setId(dto.getNeighborhoodId());
            entity.setNeighborhood(n);
        }

        return entity;
    }

    public void updateFromDTO(CampaignDTO dto, Campaign entity) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setDeleted(dto.isDeleted());

        if (dto.getNeighborhoodId() != null) {
            Neighborhood n = new Neighborhood();
            n.setId(dto.getNeighborhoodId());
            entity.setNeighborhood(n);
        }
    }

    public List<CampaignDTO> toDTOList(List<Campaign> campaigns) {
        if (campaigns == null) return null;
        return campaigns.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
