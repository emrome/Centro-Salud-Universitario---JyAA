package services;

import daos.ReportDAO;
import dtos.ReportDTO;
import exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import mappers.ReportMapper;
import models.Report;
import utils.TransactionHelper;

import java.util.List;

@RequestScoped
public class ReportService {

    @Inject
    private ReportDAO reportDAO;

    @Inject
    private ReportMapper reportMapper;

    @Inject
    private TransactionHelper txHelper;

    public ReportDTO getById(Long id) {
        Report report = validateExists(id);
        return reportMapper.toDTO(report);
    }

    public List<ReportDTO> getAll() {
        return reportMapper.toDTOList(reportDAO.findAll());
    }

    public ReportDTO create(ReportDTO dto) {
        Report entity = reportMapper.fromDTO(dto);
        reportDAO.save(entity);
        return reportMapper.toDTO(entity);
    }

    public ReportDTO update(Long id, ReportDTO dto) {
        Report report = validateExists(id);
        reportMapper.updateFromDTO(dto, report);
        reportDAO.update(report);
        return reportMapper.toDTO(report);
    }

    public void delete(Long id) {
        txHelper.executeInTransaction(() -> {
            Report r = validateExists(id);
            reportDAO.delete(r);
        });
    }

    public ReportDTO attachFile(Long id, byte[] content, String fileName, String fileMime) {
        Report report = validateExists(id);
        report.setFileContent(content);
        report.setFileName(fileName);
        report.setFileMime(fileMime);
        reportDAO.update(report);
        return reportMapper.toDTO(report);
    }

    public ReportDTO shareWithRepresentative(Long reportId, Long representativeId) {
        Report report = validateExists(reportId);
        ReportDTO dto = reportMapper.toDTO(report);
        if (dto.getSharedWithIds() == null || !dto.getSharedWithIds().contains(representativeId)) {
            dto.getSharedWithIds().add(representativeId);
            reportMapper.updateFromDTO(dto, report);
            reportDAO.update(report);
        }
        return reportMapper.toDTO(report);
    }

    public List<ReportDTO> getPublicReports() {
        return reportMapper.toDTOList(reportDAO.findAllPublic());
    }

    public Report validateExistsForDownload(Long id) { return validateExists(id); }

    public ReportDTO revokeShareWithRepresentative(Long reportId, Long representativeId) {
        Report report = validateExists(reportId);
        ReportDTO dto = reportMapper.toDTO(report);
        if (dto.getSharedWithIds() != null && dto.getSharedWithIds().contains(representativeId)) {
            dto.getSharedWithIds().remove(representativeId);
            reportMapper.updateFromDTO(dto, report);
            reportDAO.update(report);
        }
        return reportMapper.toDTO(report);
    }

    private Report validateExists(Long id) {
        Report r = reportDAO.findById(id);
        if (r == null) throw new ResourceNotFoundException("Report not found");
        return r;
    }
}
