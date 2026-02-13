package services;

import daos.CampaignDAO;
import daos.NeighborhoodDAO;
import dtos.CampaignDTO;
import exceptions.DuplicateResourceException;
import exceptions.InvalidDataException;
import exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import mappers.CampaignMapper;
import models.Campaign;
import models.Event;
import models.Neighborhood;
import utils.TransactionHelper;

import java.time.LocalDate;
import java.util.List;

@RequestScoped
public class CampaignService {

    @Inject
    private CampaignDAO campaignDAO;

    @Inject
    private NeighborhoodDAO neighborhoodDAO;

    @Inject
    private CampaignMapper campaignMapper;

    @Inject
    private TransactionHelper txHelper;

    public CampaignDTO getById(Long id) {
        return campaignMapper.toDTO(validateCampaignExists(id));
    }

    public List<CampaignDTO> getAll() {
        return campaignMapper.toDTOList(campaignDAO.findAll());
    }

    public CampaignDTO create(CampaignDTO dto) {
        if (campaignDAO.findByName(dto.getName()) != null) {
            throw new DuplicateResourceException("Campaign named '" + dto.getName() + "' already exists");
        }
        validateCampaignDates(dto.getStartDate(), dto.getEndDate());
        Campaign campaign = campaignMapper.fromDTO(dto);

        Neighborhood neighborhood = neighborhoodDAO.findById(dto.getNeighborhoodId());
        if (neighborhood == null) {
            throw new ResourceNotFoundException("Neighborhood with ID " + dto.getNeighborhoodId() + " not found");
        }
        campaign.setNeighborhood(neighborhood);

        campaignDAO.save(campaign);
        return campaignMapper.toDTO(campaign);
    }

    public CampaignDTO update(Long id, CampaignDTO dto) {
        Campaign campaign = validateCampaignExists(id);

        String newName = dto.getName();
        String currentName = campaign.getName();

        if (newName != null && !newName.equals(currentName)) {
            Campaign existing = campaignDAO.findByName(newName);
            if (existing != null && !existing.getId().equals(id)) {
                throw new DuplicateResourceException("Campaign named '" + newName + "' already exists");
            }
        }

        validateCampaignDates(dto.getStartDate(), dto.getEndDate());
        validateNewDatesDoNotExcludeEvents(campaign, dto.getStartDate(), dto.getEndDate());

        campaignMapper.updateFromDTO(dto, campaign);

        if (dto.getNeighborhoodId() != null) {
            Neighborhood neighborhood = neighborhoodDAO.findById(dto.getNeighborhoodId());
            if (neighborhood == null) {
                throw new ResourceNotFoundException("Neighborhood with ID " + dto.getNeighborhoodId() + " not found");
            }
            campaign.setNeighborhood(neighborhood);
        }

        campaignDAO.update(campaign);
        return campaignMapper.toDTO(campaign);
    }

    public void delete(Long id) {
        txHelper.executeInTransaction(() -> {
            Campaign c = validateCampaignExists(id);
            campaignDAO.delete(c);
        });
    }

    private Campaign validateCampaignExists(Long id) {
        Campaign c = campaignDAO.findById(id);
        if (c == null) {
            throw new ResourceNotFoundException("Campaign not found");
        }
        return c;
    }

    private void validateCampaignDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidDataException("Start date and end date must not be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDataException("Start date cannot be after end date");
        }
    }

    private void validateNewDatesDoNotExcludeEvents(Campaign campaign, LocalDate newStart, LocalDate newEnd) {
        if (campaign.getEvents() == null || campaign.getEvents().isEmpty()) return;

        var eventDates = campaign.getEvents().stream()
                .filter(e -> !e.isDeleted())
                .map(Event::getDate)
                .toList();

        if (eventDates.isEmpty()) return;

        LocalDate minEvent = eventDates.stream().min(LocalDate::compareTo).orElse(null);
        LocalDate maxEvent = eventDates.stream().max(LocalDate::compareTo).orElse(null);

        boolean violatesStart = newStart != null && minEvent != null && minEvent.isBefore(newStart);
        boolean violatesEnd   = newEnd   != null && maxEvent != null && maxEvent.isAfter(newEnd);

        if (violatesStart || violatesEnd) {
            long beforeCount = eventDates.stream().filter(d -> newStart != null && d.isBefore(newStart)).count();
            long afterCount  = eventDates.stream().filter(d -> newEnd   != null && d.isAfter(newEnd)).count();

            String msg = "No se pueden actualizar las fechas de la campaña porque hay jornadas fuera del nuevo rango. "
                    + (violatesStart ? ("• " + beforeCount + " jornada(s) antes de la nueva fecha de inicio (" + newStart + "). Primera: " + minEvent + ". ") : "")
                    + (violatesEnd   ? ("• " + afterCount  + " jornada(s) después de la nueva fecha de fin (" + newEnd + "). Última: " + maxEvent + ".") : "");

            throw new InvalidDataException(msg.trim());
        }
    }

}
