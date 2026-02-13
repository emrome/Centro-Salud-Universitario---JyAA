package services;

import daos.ReportDAO;
import daos.ReportRequestDAO;
import dtos.ReportDTO;
import dtos.ReportRequestDTO;
import exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import mappers.ReportMapper;
import mappers.ReportRequestMapper;
import models.Report;
import models.ReportRequest;
import models.enums.RequestStatus;
import models.people.HealthStaff;
import models.people.SocialOrgRepresentative;

import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class ReportRequestService {

    @Inject
    private ReportRequestDAO reportRequestDAO;

    @Inject
    private ReportDAO reportDAO;

    @Inject
    private ReportRequestMapper reportRequestMapper;

    @Inject
    private ReportMapper reportMapper;

    @Inject
    private EntityManager em;

    public ReportRequestDTO getById(Long id) {
        ReportRequest rr = validateExists(id);
        return reportRequestMapper.toDTO(rr);
    }

    public List<ReportRequestDTO> getByRequester(Long requesterId) {
        return reportRequestMapper.toDTOList(reportRequestDAO.findByRequesterId(requesterId));
    }

    public List<ReportRequestDTO> getPendingForHealth() {
        return reportRequestMapper.toDTOList(reportRequestDAO.findAllPending());
    }

    public List<ReportRequestDTO> getResolvedForHealth() {
        return reportRequestMapper.toDTOList(reportRequestDAO.findByStatus(RequestStatus.COMPLETED));
    }

    public List<ReportRequestDTO> getRejectedForHealth() {
        return reportRequestMapper.toDTOList(reportRequestDAO.findByStatus(RequestStatus.REJECTED));
    }

    public ReportRequestDTO create(ReportRequestDTO dto) {
        if (dto == null || dto.getRequesterId() == null) {
            throw new IllegalArgumentException("RequesterId required");
        }
        ReportRequest rr = new ReportRequest();
        SocialOrgRepresentative repRef = em.getReference(SocialOrgRepresentative.class, dto.getRequesterId());
        rr.setRequester(repRef);
        rr.setDescription(dto.getDescription());
        rr.setStatus(RequestStatus.PENDING);
        reportRequestDAO.save(rr);
        return reportRequestMapper.toDTO(rr);
    }

    public ReportRequestDTO completeWithReport(Long requestId, ReportDTO reportDTO, byte[] fileContent, String fileName, String fileMime, Long authorId) {
        ReportRequest rr = validateExists(requestId);
        if (rr.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalStateException("Cannot complete a rejected request");
        }

        ReportDTO meta = reportDTO != null ? reportDTO : new ReportDTO();
        Report report = reportMapper.fromDTO(meta);
        report.setId(null);
        report.setFileContent(fileContent);
        report.setFileName(fileName);
        report.setFileMime(fileMime);
        if (reportDTO == null) {
            report.setVisibleToAllHealthStaff(true);
        }

        report.setPublicVisible(meta.isPublicVisible());

        HealthStaff authorRef = em.getReference(HealthStaff.class, authorId);
        report.setAuthor(authorRef);

        reportDAO.save(report);

        rr.setReport(report);
        rr.setStatus(RequestStatus.COMPLETED);
        rr.setResolvedBy(authorRef);
        rr.setResolvedAt(LocalDateTime.now());
        reportRequestDAO.update(rr);

        return reportRequestMapper.toDTO(rr);
    }

    public ReportRequestDTO reject(Long id, String reason, Long resolverId) {
        ReportRequest rr = validateExists(id);
        rr.setStatus(RequestStatus.REJECTED);
        rr.setResolvedAt(LocalDateTime.now());
        if (reason != null && !reason.isBlank()) {
            String base = rr.getDescription() == null ? "" : rr.getDescription() + " ";
            rr.setDescription((base + "[Rechazo: " + reason + "]").trim());
        }
        HealthStaff resolverRef = em.getReference(HealthStaff.class, resolverId);
        rr.setResolvedBy(resolverRef);
        reportRequestDAO.update(rr);
        return reportRequestMapper.toDTO(rr);
    }

    private ReportRequest validateExists(Long id) {
        ReportRequest rr = reportRequestDAO.findById(id);
        if (rr == null) throw new ResourceNotFoundException("ReportRequest not found");
        return rr;
    }
}

