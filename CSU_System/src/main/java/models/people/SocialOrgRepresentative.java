package models.people;
import java.util.List;
import models.ReportRequest;

import models.Organization;
import jakarta.persistence.*;

@Entity
public class SocialOrgRepresentative extends User {

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@OneToMany(mappedBy = "requester", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private List<ReportRequest> requests;

    public SocialOrgRepresentative() {
        super();
    }

	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public List<ReportRequest> getRequests() {
		return requests;
	}
	public void setRequests(List<ReportRequest> requests) {
		this.requests = requests;
	}
}
