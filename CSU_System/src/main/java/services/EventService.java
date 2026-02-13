package services;

import daos.CampaignDAO;
import daos.EventDAO;
import daos.ZoneDAO;
import daos.people.SurveyorDAO;
import dtos.EventDTO;
import dtos.people.SurveyorDTO;
import exceptions.DuplicateResourceException;
import exceptions.InvalidDataException;
import exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import mappers.EventMapper;
import mappers.people.SurveyorMapper;
import models.Campaign;
import models.Event;
import models.people.Surveyor;
import models.Zone;
import utils.TransactionHelper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class EventService {

    @Inject private EventDAO eventDAO;
    @Inject private ZoneDAO zoneDAO;
    @Inject private SurveyorDAO surveyorDAO;
    @Inject private CampaignDAO campaignDAO;
    @Inject private EventMapper eventMapper;
    @Inject private SurveyorMapper surveyorMapper;
    @Inject private TransactionHelper txHelper;

    public List<EventDTO> getAll(Long campaignId) {
        Campaign campaign = validateCampaignExists(campaignId);
        List<Event> events = campaign.getEvents().stream()
                .filter(e -> !e.isDeleted())
                .collect(Collectors.toList());
        return eventMapper.toDTOList(events);
    }

    public EventDTO getById(Long campaignId, Long eventId) {
        validateEventBelongsToCampaign(campaignId, eventId);
        Event event = validateEventExists(eventId);
        return eventMapper.toDTO(event);
    }

    public EventDTO create(Long campaignId, EventDTO dto) {
        Campaign campaign = validateCampaignExists(campaignId);
        validateEventDate(campaign, dto.getDate());

        Zone zone = validateZoneExists(dto.getZoneId());
        List<Surveyor> surveyors = getValidatedSurveyors(dto.getSurveyorIds());

        if (hasDuplicates(dto.getSurveyorIds())) {
            throw new DuplicateResourceException("Duplicate surveyor IDs not allowed in the same event");
        }

        Event event = eventMapper.fromDTO(dto, zone, surveyors, campaign);
        eventDAO.save(event);
        return eventMapper.toDTO(event);
    }

    public EventDTO update(Long campaignId, Long eventId, EventDTO dto) {
        validateEventBelongsToCampaign(campaignId, eventId);

        Campaign campaign = validateCampaignExists(campaignId);
        validateEventDate(campaign, dto.getDate());

        Event event = validateEventExists(eventId);
        Zone zone = validateZoneExists(dto.getZoneId());
        List<Surveyor> surveyors = getValidatedSurveyors(dto.getSurveyorIds());

        if (hasDuplicates(dto.getSurveyorIds())) {
            throw new DuplicateResourceException("Duplicate surveyor IDs not allowed in the same event");
        }

        eventMapper.updateFromDTO(dto, event, zone, surveyors);
        eventDAO.update(event);
        return eventMapper.toDTO(event);
    }

    public void delete(Long campaignId, Long eventId) {
        validateEventBelongsToCampaign(campaignId, eventId);
        txHelper.executeInTransaction(() -> {
            Event event = validateEventExists(eventId);
            eventDAO.delete(event);
        });
    }

    public List<SurveyorDTO> getSurveyorsForEvent(Long eventId) {
        validateEventExists(eventId);
        List<Surveyor> surveyors = surveyorDAO.findAllByEvent(eventId);
        return surveyors.stream()
                .map(surveyorMapper::toDTO)
                .collect(Collectors.toList());
    }

    private Campaign validateCampaignExists(Long id) {
        Campaign c = campaignDAO.findById(id);
        if (c == null) throw new ResourceNotFoundException("Campaign not found");
        return c;
    }

    private Event validateEventExists(Long id) {
        Event event = eventDAO.findById(id);
        if (event == null) throw new ResourceNotFoundException("Event not found");
        return event;
    }

    private Zone validateZoneExists(Long zoneId) {
        Zone zone = zoneDAO.findById(zoneId);
        if (zone == null) throw new ResourceNotFoundException("Zone not found");
        return zone;
    }

    private List<Surveyor> getValidatedSurveyors(List<Long> ids) {
        return ids.stream().map(this::validateSurveyorExists).collect(Collectors.toList());
    }

    private Surveyor validateSurveyorExists(Long id) {
        Surveyor s = surveyorDAO.findById(id);
        if (s == null) {
            throw new ResourceNotFoundException("Surveyor with ID " + id + " not found");
        }
        return s;
    }

    private boolean hasDuplicates(List<Long> ids) {
        return ids.size() != new HashSet<>(ids).size();
    }

    private void validateEventDate(Campaign campaign, LocalDate date) {
        if (date == null) {
            throw new InvalidDataException("Event date cannot be null");
        }
        if (date.isBefore(campaign.getStartDate()) || date.isAfter(campaign.getEndDate())) {
            throw new InvalidDataException("Event date must be within the campaign period");
        }
    }

    private void validateEventBelongsToCampaign(Long campaignId, Long eventId) {
        Event event = validateEventExists(eventId);
        if (!event.getCampaign().getId().equals(campaignId)) {
            throw new InvalidDataException("The event does not belong to the specified campaign");
        }
    }
}