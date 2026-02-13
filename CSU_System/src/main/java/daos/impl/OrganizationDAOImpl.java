package daos.impl;

import daos.OrganizationDAO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import models.Neighborhood;
import models.Organization;
import models.ReportRequest;
import models.people.SocialOrgRepresentative;

import java.util.List;

@ApplicationScoped
public class OrganizationDAOImpl extends GenericDAOImpl<Organization> implements OrganizationDAO {

    @Inject
    public OrganizationDAOImpl(EntityManager em) {
        super(Organization.class, em);
    }

    @Override
    public Organization findByName(String name) {
        TypedQuery<Organization> q = em.createQuery(
                "SELECT o FROM Organization o " +
                        "WHERE o.isDeleted = FALSE AND LOWER(o.name) = :name",
                Organization.class
        );
        q.setParameter("name", name.toLowerCase());
        List<Organization> result = q.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public void delete(Organization entity) {
        Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        Organization attached = em.find(Organization.class, id);
        if (attached != null && !attached.isDeleted()) {
            attached.setDeleted(true);

            if (attached.getRepresentatives() != null) {
                for (SocialOrgRepresentative rep : attached.getRepresentatives()) {
                    if (!rep.isDeleted()) {
                        rep.setDeleted(true);
                        if (rep.getRequests() != null) {
                            for (ReportRequest req : rep.getRequests()) {
                                req.setDeleted(true);
                            }
                        }
                    }
                }
            }
        }
    }
}