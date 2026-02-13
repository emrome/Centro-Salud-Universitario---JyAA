package mappers;

import dtos.EventDTO;
import jakarta.enterprise.context.ApplicationScoped;
import models.Campaign;
import models.Event;
import models.people.Surveyor;
import models.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventMapper {

    public EventDTO toDTO(Event event) {
        if (event == null) return null;

        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setDate(event.getDate());
        dto.setZoneId(event.getZone() != null ? event.getZone().getId() : null);
        dto.setSurveyorIds(event.getSurveyors() != null
                ? event.getSurveyors().stream()
                .map(Surveyor::getId)
                .collect(Collectors.toList())
                : new ArrayList<>());

        return dto;
    }

    public List<EventDTO> toDTOList(List<Event> events) {
        if (events == null) return null;
        return events.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateFromDTO(EventDTO dto, Event existing, Zone zone, List<Surveyor> surveyors) {
        if (dto == null || existing == null) return;
        existing.setDate(dto.getDate());
        existing.setZone(zone);
        existing.setSurveyors(surveyors);
    }

    public Event fromDTO(EventDTO dto, Zone zone, List<Surveyor> surveyors, Campaign campaign) {
        if (dto == null) return null;

        Event event = new Event();
        event.setDate(dto.getDate());
        event.setZone(zone);
        campaign.addEvent(event);
        event.setCampaign(campaign);
        event.setSurveyors(surveyors);
        return event;
    }
}
