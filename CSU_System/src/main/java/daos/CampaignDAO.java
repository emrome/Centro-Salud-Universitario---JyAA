package daos;

import models.Campaign;

public interface CampaignDAO extends GenericDAO<Campaign> {
    Campaign findByName(String name);
}