package mappers;

import dtos.ReportRequestDTO;
import jakarta.enterprise.context.ApplicationScoped;
import models.ReportRequest;
import models.people.HealthStaff;
import models.people.SocialOrgRepresentative;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReportRequestMapper {

    public ReportRequestDTO toDTO(ReportRequest e) {
        if (e == null) return null;
        ReportRequestDTO dto = new ReportRequestDTO();
        dto.setId(e.getId());
        dto.setReportId(e.getReport() != null ? e.getReport().getId() : null);
        SocialOrgRepresentative req = e.getRequester();
        if (req != null) {
            dto.setRequesterId(req.getId());
            String rn = trim(req.getFirstName()) + " " + trim(req.getLastName());
            dto.setRequesterName(rn.trim());
        }
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setDescription(e.getDescription());
        HealthStaff rb = e.getResolvedBy();
        if (rb != null) {
            dto.setResolvedById(rb.getId());
            String hn = trim(rb.getFirstName()) + " " + trim(rb.getLastName());
            dto.setResolvedByName(hn.trim());
        }
        dto.setResolvedAt(e.getResolvedAt());
        return dto;
    }

    public List<ReportRequestDTO> toDTOList(List<ReportRequest> list) {
        return list == null ? null : list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }
}
