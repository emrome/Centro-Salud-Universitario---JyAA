package mappers;

import dtos.ReportDTO;
import jakarta.enterprise.context.ApplicationScoped;
import models.Report;
import models.people.HealthStaff;
import models.people.SocialOrgRepresentative;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReportMapper {

    public ReportDTO toDTO(Report entity) {
        if (entity == null) return null;

        ReportDTO dto = new ReportDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());

        if (entity.getAuthor() != null) {
            dto.setAuthorId(entity.getAuthor().getId());
            String fullName = (entity.getAuthor().getFirstName() != null ? entity.getAuthor().getFirstName() : "")
                    + " "
                    + (entity.getAuthor().getLastName() != null ? entity.getAuthor().getLastName() : "");
            dto.setAuthorName(fullName.trim());
        }

        dto.setVisibleToAllHealthStaff(entity.isVisibleToAllHealthStaff());
        dto.setPublicVisible(entity.isPublicVisible());
        dto.setCreatedDate(entity.getCreatedDate());

        Set<Long> sharedIds = entity.getSharedWith() != null
                ? entity.getSharedWith().stream()
                .filter(r -> r != null && r.getId() != null)
                .map(SocialOrgRepresentative::getId)
                .collect(Collectors.toSet())
                : null;
        dto.setSharedWithIds(sharedIds);

        boolean hasFile = entity.getFileContent() != null && entity.getFileContent().length > 0;
        dto.setHasFile(hasFile);
        dto.setFileName(entity.getFileName());
        dto.setFileMime(entity.getFileMime());
        if (entity.getId() != null) {
            dto.setDownloadUrl("/api/reports/" + entity.getId() + "/file");
        }

        return dto;
    }

    public Report fromDTO(ReportDTO dto) {
        if (dto == null) return null;

        Report entity = new Report();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setVisibleToAllHealthStaff(dto.isVisibleToAllHealthStaff());
        entity.setPublicVisible(dto.isPublicVisible());

        if (dto.getAuthorId() != null) {
            HealthStaff hs = new HealthStaff();
            hs.setId(dto.getAuthorId());
            entity.setAuthor(hs);
        }

        if (dto.getSharedWithIds() != null && !dto.getSharedWithIds().isEmpty()) {
            Set<SocialOrgRepresentative> reps = dto.getSharedWithIds().stream().map(id -> {
                SocialOrgRepresentative r = new SocialOrgRepresentative();
                r.setId(id);
                return r;
            }).collect(Collectors.toSet());
            entity.setSharedWith(reps);
        }

        return entity;
    }

    public void updateFromDTO(ReportDTO dto, Report entity) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setVisibleToAllHealthStaff(dto.isVisibleToAllHealthStaff());
        entity.setVisibleToAllHealthStaff(dto.isVisibleToAllHealthStaff());
        entity.setPublicVisible(dto.isPublicVisible());

        if (dto.getAuthorId() != null) {
            HealthStaff hs = new HealthStaff();
            hs.setId(dto.getAuthorId());
            entity.setAuthor(hs);
        }

        if (dto.getSharedWithIds() != null) {
            Set<SocialOrgRepresentative> reps = dto.getSharedWithIds().stream().map(id -> {
                SocialOrgRepresentative r = new SocialOrgRepresentative();
                r.setId(id);
                return r;
            }).collect(Collectors.toSet());
            entity.setSharedWith(reps);
        }
    }

    public List<ReportDTO> toDTOList(List<Report> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
