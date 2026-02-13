package daos.people;

import models.people.SocialOrgRepresentative;

import java.util.List;

public interface SocialOrgRepresentativeDAO extends UserDAO<SocialOrgRepresentative> {
    List<SocialOrgRepresentative> findByOrganizationId(Long organizationId);
}