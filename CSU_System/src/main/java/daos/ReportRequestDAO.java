package daos;

import models.ReportRequest;
import models.enums.RequestStatus;

import java.util.List;

public interface ReportRequestDAO extends GenericDAO<ReportRequest> {
    List<ReportRequest> findByRequesterId(Long requesterId);
    List<ReportRequest> findAllPending();
    List<ReportRequest> findByStatus(RequestStatus status);
}
