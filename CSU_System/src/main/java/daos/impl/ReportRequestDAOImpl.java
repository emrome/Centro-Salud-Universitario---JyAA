package daos.impl;

import daos.ReportRequestDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import models.ReportRequest;
import models.enums.RequestStatus;

import java.util.List;

@RequestScoped
public class ReportRequestDAOImpl extends GenericDAOImpl<ReportRequest> implements ReportRequestDAO {

    @Inject
    public ReportRequestDAOImpl(EntityManager em) {
        super(ReportRequest.class, em);
    }

    @Override
    public List<ReportRequest> findByRequesterId(Long requesterId) {
        return em.createQuery("SELECT r FROM ReportRequest r WHERE r.requester.id = :rid", ReportRequest.class)
                .setParameter("rid", requesterId)
                .getResultList();
    }


    @Override
    public List<ReportRequest> findAllPending() {
        return em.createQuery("SELECT r FROM ReportRequest r WHERE r.status = :st AND r.isDeleted = false ", ReportRequest.class)
                .setParameter("st", RequestStatus.PENDING)
                .getResultList();
    }

    public List<ReportRequest> findByStatus(RequestStatus status) {
        return em.createQuery("SELECT r FROM ReportRequest r WHERE r.status = :st ORDER BY r.resolvedAt DESC", ReportRequest.class)
                .setParameter("st", status)
                .getResultList();
    }
}
