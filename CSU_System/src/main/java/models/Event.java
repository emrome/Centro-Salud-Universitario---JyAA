package models;
import java.util.List;

import models.people.Surveyor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Event extends BaseEntity {

	private LocalDate date;

	@ManyToOne
	@JoinColumn(name = "zone_id")
	private Zone zone;

	@ManyToMany
	@JoinTable(
			name = "event_surveyor",
			joinColumns = @JoinColumn(name = "event_id"),
			inverseJoinColumns = @JoinColumn(name = "surveyor_id")
	)
	private List<Surveyor> surveyors;

	@ManyToOne
	@JoinColumn(name = "campaign_id", nullable = false)
	private Campaign campaign;
	
	public Event() {
		super();
	}

	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public Zone getZone() {
		return zone;
	}
	public void setZone(Zone zone) {
		this.zone = zone;
	}
	public List<Surveyor> getSurveyors() {
		return surveyors;
	}
	public void setSurveyors(List<Surveyor> surveyors) {
		this.surveyors = surveyors;
	}
	public void addSurveyor(Surveyor surveyor) {
		this.surveyors.add(surveyor);
	}
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
}