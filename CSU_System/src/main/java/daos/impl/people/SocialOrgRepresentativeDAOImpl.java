package daos.impl.people;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.people.SocialOrgRepresentative;
import models.ReportRequest;
import daos.people.SocialOrgRepresentativeDAO;

import jakarta.persistence.EntityManager;

import java.util.List;

@RequestScoped
public class SocialOrgRepresentativeDAOImpl extends UserDAOImpl<SocialOrgRepresentative> implements SocialOrgRepresentativeDAO {
    @Inject
    public SocialOrgRepresentativeDAOImpl(EntityManager em) {
        super(SocialOrgRepresentative.class, em);
    }

    @Override
    public void delete(SocialOrgRepresentative entity) {
        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        SocialOrgRepresentative attached = em.find(SocialOrgRepresentative.class, id);

        if (attached != null && !attached.isDeleted()) {
            attached.setDeleted(true);

            if (attached.getRequests() != null) {
                for (ReportRequest req : attached.getRequests()) {
                    req.setDeleted(true);
                }
            }
        }
    }
    @Override
    public List<SocialOrgRepresentative> findByOrganizationId(Long orgId) {
        return em.createQuery(
                        "SELECT r FROM SocialOrgRepresentative r " +
                                "WHERE r.isDeleted = false AND r.organization.id = :orgId", SocialOrgRepresentative.class)
                .setParameter("orgId", orgId)
                .getResultList();
    }
}