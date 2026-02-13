package daos;

import models.Event;
import models.Organization;

import java.util.List;

public interface OrganizationDAO extends GenericDAO<Organization> {

    Organization findByName(String name);
}
