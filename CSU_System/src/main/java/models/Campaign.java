package models;
import models.survey.Survey;

import java.util.List;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class Campaign extends BaseEntity {
	private String name;
	private LocalDate startDate;
	private LocalDate endDate;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "neighborhood_id")
	private Neighborhood neighborhood;

	@OneToMany(mappedBy = "campaign", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private List<Event> events;

	@OneToOne()
	private Survey survey;
	
    public Campaign() {
		super();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public Neighborhood getNeighborhood() {
		return neighborhood;
	}
	public void setNeighborhood(Neighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	public void addEvent(Event event) {
		this.events.add(event);
	}
	public void deleteEvent(Event event) {
		for (int i = 0; i < this.events.size(); i++) {
			if (this.events.get(i).getId().equals(event.getId())) {
				this.events.remove(i);
				return;
			}
		}
	}
	public Survey getSurvey() {
		return survey;
	}
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
}