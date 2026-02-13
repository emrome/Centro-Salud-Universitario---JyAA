package models;

import jakarta.persistence.*;
import models.enums.RequestStatus;
import models.people.HealthStaff;
import models.people.SocialOrgRepresentative;

@Entity
@Table(name = "ReportRequest")
public class ReportRequest extends BaseEntity {
	@OneToOne
	@JoinColumn(name = "report_id")
	private Report report;

	@ManyToOne
	@JoinColumn(name = "requester_id", nullable = false)
	private SocialOrgRepresentative requester;

	@Enumerated(EnumType.STRING)
	private RequestStatus status;

	private String description;

	@ManyToOne
	@JoinColumn(name = "resolved_by_id")
	private HealthStaff resolvedBy;

	private java.time.LocalDateTime resolvedAt;

	public ReportRequest() { super(); }

	public Report getReport() { return report; }
	public void setReport(Report report) { this.report = report; }
	public SocialOrgRepresentative getRequester() { return requester; }
	public void setRequester(SocialOrgRepresentative requester) { this.requester = requester; }
	public RequestStatus getStatus() { return status; }
	public void setStatus(RequestStatus status) { this.status = status; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public HealthStaff getResolvedBy() { return resolvedBy; }
	public void setResolvedBy(HealthStaff resolvedBy) { this.resolvedBy = resolvedBy; }
	public java.time.LocalDateTime getResolvedAt() { return resolvedAt; }
	public void setResolvedAt(java.time.LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
