package daos.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.Report;
import daos.ReportDAO;

import jakarta.persistence.EntityManager;

import java.util.List;

@RequestScoped
public class ReportDAOImpl extends GenericDAOImpl<Report> implements ReportDAO {
    @Inject
    public ReportDAOImpl(EntityManager em) {
        super(Report.class, em);
    }

    @Override
    public List<Report> findAllPublic() {
        return em.createQuery(
                "SELECT r FROM Report r " +
                        "WHERE r.publicVisible = true AND r.isDeleted = false " +
                        "ORDER BY r.createdDate DESC",
                Report.class
        ).getResultList();
    }
}