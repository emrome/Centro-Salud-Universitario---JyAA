package daos.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import models.Campaign;
import daos.CampaignDAO;

import jakarta.persistence.EntityManager;

@RequestScoped
public class CampaignDAOImpl extends GenericDAOImpl<Campaign> implements CampaignDAO {
    @Inject
    public CampaignDAOImpl(EntityManager em) {
        super(Campaign.class, em);
    }

    @Override
    public Campaign findById(Long id) {
        return em.createQuery(
                        "SELECT c FROM Campaign c " +
                                "LEFT JOIN FETCH c.survey " +
                                "LEFT JOIN FETCH c.events " +
                                "WHERE c.id = :id AND c.isDeleted = false", Campaign.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Campaign findByName(String name) {
        return em.createQuery("SELECT c FROM Campaign c WHERE c.name = :name AND c.isDeleted = false", Campaign.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);

    }
}